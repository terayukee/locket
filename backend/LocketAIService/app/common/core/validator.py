from fastapi import UploadFile
from PIL import Image
from io import BytesIO
from PyPDF2 import PdfReader
from app.domain.receipt.exception.receipt_exception import ReceiptException
from app.domain.receipt.constant.receipt_error import ReceiptErrorCode
import logging

logger = logging.getLogger(__name__)

class FileValidator:
    ALLOWED_EXTENSIONS = {'jpg', 'jpeg', 'png'}
    ALLOWED_PDF_EXTENSION = {'pdf'}
    MAX_FILE_SIZE = 5 * 1024 * 1024  # 5MB
    MIN_IMAGE_SIZE = (300, 300)  # 최소 300x300 픽셀

    @staticmethod
    async def validate_image(file: UploadFile):
        """이미지 파일 검증"""
        try:
            logger.info(f"검증 시작: {file.filename}")

            # 파일 이름 검사
            if not file.filename:
                raise ReceiptException(error_code=ReceiptErrorCode.FILE_NOT_FOUND)

            # 파일 확장자 검사
            extension = file.filename.split('.')[-1].lower()
            if extension not in FileValidator.ALLOWED_EXTENSIONS:
                raise ReceiptException(error_code=ReceiptErrorCode.INVALID_EXTENSION)

            # 파일 내용 읽기
            contents = await file.read()
            if not contents:
                raise ReceiptException(error_code=ReceiptErrorCode.EMPTY_FILE)

            # 파일 크기 검사
            file_size = len(contents)
            logger.info(f"파일 크기: {file_size} bytes")
            if file_size > FileValidator.MAX_FILE_SIZE:
                raise ReceiptException(error_code=ReceiptErrorCode.FILE_TOO_LARGE)

            try:
                # 이미지 파일 검증
                image = Image.open(BytesIO(contents))
                width, height = image.size
                logger.info(f"이미지 크기: {width}x{height}")

                if width < FileValidator.MIN_IMAGE_SIZE[0] or height < FileValidator.MIN_IMAGE_SIZE[1]:
                    raise ReceiptException(error_code=ReceiptErrorCode.LOW_RESOLUTION)

            except ReceiptException:
                raise
            except Exception as e:
                logger.error(f"이미지 검증 오류: {str(e)}")
                raise ReceiptException(error_code=ReceiptErrorCode.CORRUPTED_FILE)

        except ReceiptException:
            raise
        except Exception as e:
            logger.error(f"파일 검증 중 오류: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.FILE_NOT_FOUND)

        finally:
            await file.seek(0)  # 파일 포인터 초기화

    @staticmethod
    async def validate_pdf(file: UploadFile):
        """PDF 파일 검증"""
        try:
            logger.info(f"PDF 검증 시작: {file.filename}")

            # 파일 이름 검사
            if not file.filename:
                raise ReceiptException(error_code=ReceiptErrorCode.FILE_NOT_FOUND)

            # 파일 확장자 검사
            extension = file.filename.split('.')[-1].lower()
            if extension not in FileValidator.ALLOWED_PDF_EXTENSION:
                raise ReceiptException(error_code=ReceiptErrorCode.INVALID_EXTENSION)

            # 파일 내용 읽기
            contents = await file.read()
            if not contents:
                raise ReceiptException(error_code=ReceiptErrorCode.EMPTY_FILE)

            # 파일 크기 검사
            file_size = len(contents)
            if file_size > FileValidator.MAX_FILE_SIZE:
                raise ReceiptException(error_code=ReceiptErrorCode.FILE_TOO_LARGE)

            # PDF 페이지 수 검사
            try:
                pdf = PdfReader(BytesIO(contents))
                if len(pdf.pages) > 1:
                    raise ReceiptException(error_code=ReceiptErrorCode.PDF_TOO_MANY_PAGES)

            except ReceiptException:
                raise
            except Exception as e:
                logger.error(f"PDF 검증 오류: {str(e)}")
                raise ReceiptException(error_code=ReceiptErrorCode.CORRUPTED_FILE)

        except ReceiptException:
            raise
        except Exception as e:
            logger.error(f"파일 검증 중 오류: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.FILE_NOT_FOUND)

        finally:
            await file.seek(0)  # 파일 포인터 초기화