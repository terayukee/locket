from fastapi import UploadFile
from PIL import Image
from io import BytesIO
from PyPDF2 import PdfReader
from ..constant.status import ErrorMessage
from app.domain.receipt.exception.exception import FileValidationException

class FileValidator:
    ALLOWED_EXTENSIONS = {'jpg', 'jpeg', 'png'}
    ALLOWED_PDF_EXTENSION = {'pdf'}
    MAX_FILE_SIZE = 5 * 1024 * 1024  # 5MB
    MIN_IMAGE_SIZE = (300, 300)  # 최소 300x300 픽셀

    @staticmethod
    async def validate_image(file: UploadFile):
        """이미지 파일 검증"""
        try:
            print(f"검증 시작: {file.filename}")  # 디버깅

            # 파일 이름 검사
            if not file.filename:
                raise FileValidationException(
                    message=ErrorMessage.FILE_NOT_FOUND,
                    detail="파일 이름이 없습니다."
                )

            # 파일 확장자 검사
            extension = file.filename.split('.')[-1].lower()
            if extension not in FileValidator.ALLOWED_EXTENSIONS:
                raise FileValidationException(
                    message=ErrorMessage.INVALID_FILE_TYPE,
                    detail=f"지원하지 않는 파일 형식입니다 ({extension}). JPG, JPEG, PNG 파일만 업로드 가능합니다."
                )

            # 파일 내용 읽기
            contents = await file.read()
            if not contents:
                raise FileValidationException(
                    message=ErrorMessage.FILE_NOT_FOUND,
                    detail="파일이 비어있습니다."
                )

            # 파일 크기 검사
            file_size = len(contents)
            print(f"파일 크기: {file_size} bytes")  # 디버깅
            if file_size > FileValidator.MAX_FILE_SIZE:
                raise FileValidationException(
                    message=ErrorMessage.FILE_TOO_LARGE,
                    detail=f"파일 크기가 너무 큽니다 ({file_size} bytes). 최대 5MB까지 업로드 가능합니다."
                )

            try:
                # 이미지 파일 검증
                image = Image.open(BytesIO(contents))
                width, height = image.size
                print(f"이미지 크기: {width}x{height}")  # 디버깅

                if width < FileValidator.MIN_IMAGE_SIZE[0] or height < FileValidator.MIN_IMAGE_SIZE[1]:
                    raise FileValidationException(
                        message=ErrorMessage.INVALID_IMAGE_SIZE,
                        detail=f"이미지 해상도가 너무 낮습니다 ({width}x{height}). 최소 300x300 픽셀 이상이어야 합니다."
                    )

            except FileValidationException:
                raise
            except Exception as e:
                print(f"이미지 검증 오류: {str(e)}")  # 디버깅
                raise FileValidationException(
                    message=ErrorMessage.INVALID_FILE_TYPE,
                    detail=f"이미지 파일 검증 중 오류 발생: {str(e)}"
                )

        except FileValidationException:
            raise
        except Exception as e:
            print(f"파일 검증 중 오류: {str(e)}")  # 디버깅
            raise FileValidationException(
                message=ErrorMessage.FILE_NOT_FOUND,
                detail=f"파일 검증 중 오류 발생: {str(e)}"
            )

        finally:
            await file.seek(0)  # 파일 포인터 초기화

    @staticmethod
    async def validate_pdf(file: UploadFile):
        """PDF 파일 검증"""
        try:
            print(f"PDF 검증 시작: {file.filename}")

            # 파일 이름 검사
            if not file.filename:
                raise FileValidationException(
                    message=ErrorMessage.FILE_NOT_FOUND,
                    detail="파일 이름이 없습니다."
                )

            # 파일 확장자 검사
            extension = file.filename.split('.')[-1].lower()
            if extension not in FileValidator.ALLOWED_PDF_EXTENSION:
                raise FileValidationException(
                    message=ErrorMessage.INVALID_FILE_TYPE,
                    detail="PDF 파일만 업로드 가능합니다."
                )

            # 파일 내용 읽기
            contents = await file.read()
            if not contents:
                raise FileValidationException(
                    message=ErrorMessage.FILE_NOT_FOUND,
                    detail="파일이 비어있습니다."
                )

            # 파일 크기 검사
            file_size = len(contents)
            if file_size > FileValidator.MAX_FILE_SIZE:
                raise FileValidationException(
                    message=ErrorMessage.FILE_TOO_LARGE,
                    detail=f"파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다."
                )

            # PDF 페이지 수 검사
            try:
                pdf = PdfReader(BytesIO(contents))
                if len(pdf.pages) > 1:
                    raise FileValidationException(
                        message=ErrorMessage.INVALID_FILE_TYPE,
                        detail="1페이지를 초과하는 PDF는 처리할 수 없습니다."
                    )

            except FileValidationException:
                raise
            except Exception as e:
                raise FileValidationException(
                    message=ErrorMessage.INVALID_FILE_TYPE,
                    detail=f"PDF 파일 검증 중 오류 발생: {str(e)}"
                )

        except FileValidationException:
            raise
        except Exception as e:
            raise FileValidationException(
                message=ErrorMessage.FILE_NOT_FOUND,
                detail=f"파일 검증 중 오류 발생: {str(e)}"
            )

        finally:
            await file.seek(0)  # 파일 포인터 초기화