from datetime import datetime
from app.common.constant.status import StatusCode
from app.domain.receipt.constant.receipt_error import ReceiptErrorCode, ReceiptErrorMessage
from app.domain.category.constant.category_error import CategoryErrorMessage
from app.domain.feedback.constant.feedback_error import FeedbackErrorCode, FeedbackErrorMessage
from .model import ErrorResponse

def get_receipt_error_examples():
    return {
        "400": {
            "model": ErrorResponse,
            "description": "파일 검증 오류",
            "content": {
                "application/json": {
                    "examples": {
                        "file_not_found": {
                            "summary": ReceiptErrorMessage.FILE_NOT_FOUND.value,
                            "value": {
                                "status": ReceiptErrorCode.FILE_NOT_FOUND.value,
                                "error": ReceiptErrorCode.FILE_NOT_FOUND.name,
                                "message": ReceiptErrorMessage.FILE_NOT_FOUND.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "file_too_large": {
                            "summary": ReceiptErrorMessage.FILE_TOO_LARGE.value,
                            "value": {
                                "status": ReceiptErrorCode.FILE_TOO_LARGE.value,
                                "error": ReceiptErrorCode.FILE_TOO_LARGE.name,
                                "message": ReceiptErrorMessage.FILE_TOO_LARGE.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "invalid_file_type": {
                            "summary": ReceiptErrorMessage.INVALID_FILE_TYPE.value,
                            "value": {
                                "status": ReceiptErrorCode.INVALID_FILE_TYPE.value,
                                "error": ReceiptErrorCode.INVALID_FILE_TYPE.name,
                                "message": ReceiptErrorMessage.INVALID_FILE_TYPE.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "invalid_image_size": {
                            "summary": ReceiptErrorMessage.INVALID_IMAGE_SIZE.value,
                            "value": {
                                "status": ReceiptErrorCode.INVALID_IMAGE_SIZE.value,
                                "error": ReceiptErrorCode.INVALID_IMAGE_SIZE.name,
                                "message": ReceiptErrorMessage.INVALID_IMAGE_SIZE.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        }
                    }
                }
            }
        },
        "500": {
            "model": ErrorResponse,
            "description": "서버 처리 오류",
            "content": {
                "application/json": {
                    "examples": {
                        "ocr_processing_error": {
                            "summary": ReceiptErrorMessage.OCR_PROCESSING_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.OCR_PROCESSING_ERROR.value,
                                "error": ReceiptErrorCode.OCR_PROCESSING_ERROR.name,
                                "message": ReceiptErrorMessage.OCR_PROCESSING_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "ocr_parsing_error": {
                            "summary": ReceiptErrorMessage.OCR_PARSING_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.OCR_PARSING_ERROR.value,
                                "error": ReceiptErrorCode.OCR_PARSING_ERROR.name,
                                "message": ReceiptErrorMessage.OCR_PARSING_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "ocr_api_error": {
                            "summary": ReceiptErrorMessage.OCR_API_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.OCR_API_ERROR.value,
                                "error": ReceiptErrorCode.OCR_API_ERROR.name,
                                "message": ReceiptErrorMessage.OCR_API_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "classification_init_error": {
                            "summary": ReceiptErrorMessage.CLASSIFICATION_INIT_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.CLASSIFICATION_INIT_ERROR.value,
                                "error": ReceiptErrorCode.CLASSIFICATION_INIT_ERROR.name,
                                "message": ReceiptErrorMessage.CLASSIFICATION_INIT_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "classification_error": {
                            "summary": ReceiptErrorMessage.CLASSIFICATION_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.CLASSIFICATION_ERROR.value,
                                "error": ReceiptErrorCode.CLASSIFICATION_ERROR.name,
                                "message": ReceiptErrorMessage.CLASSIFICATION_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "classification_mismatch_error": {
                            "summary": ReceiptErrorMessage.CLASSIFICATION_MISMATCH_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.CLASSIFICATION_MISMATCH_ERROR.value,
                                "error": ReceiptErrorCode.CLASSIFICATION_MISMATCH_ERROR.name,
                                "message": ReceiptErrorMessage.CLASSIFICATION_MISMATCH_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "classification_invalid_category": {
                            "summary": ReceiptErrorMessage.CLASSIFICATION_INVALID_CATEGORY.value,
                            "value": {
                                "status": ReceiptErrorCode.CLASSIFICATION_INVALID_CATEGORY.value,
                                "error": ReceiptErrorCode.CLASSIFICATION_INVALID_CATEGORY.name,
                                "message": ReceiptErrorMessage.CLASSIFICATION_INVALID_CATEGORY.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        },
                        "classification_parse_error": {
                            "summary": ReceiptErrorMessage.CLASSIFICATION_PARSE_ERROR.value,
                            "value": {
                                "status": ReceiptErrorCode.CLASSIFICATION_PARSE_ERROR.value,
                                "error": ReceiptErrorCode.CLASSIFICATION_PARSE_ERROR.name,
                                "message": ReceiptErrorMessage.CLASSIFICATION_PARSE_ERROR.value,
                                "timestamp": "2024-04-04T15:43:11.813Z"
                            }
                        }
                    }
                }
            }
        }
    }

def get_category_error_examples():
    return {
        "400": {
            "model": ErrorResponse,
            "description": "잘못된 요청",
            "content": {
                "application/json": {
                    "example": {
                        "status": 400,
                        "error": "BAD_REQUEST",
                        "message": "잘못된 요청입니다",
                        "timestamp": "2024-04-04T15:43:11.813Z"
                    }
                }
            }
        },
        "500": {
            "model": ErrorResponse,
            "description": "서버 오류",
            "content": {
                "application/json": {
                    "example": {
                        "status": 500,
                        "error": "INTERNAL_ERROR",
                        "message": "서비스 처리 중 오류가 발생했습니다",
                        "timestamp": "2024-04-04T15:43:11.813Z"
                    }
                }
            }
        }
    }

