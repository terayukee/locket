from enum import Enum

class Category(str, Enum):
    FOOD = "식비"
    CAFE = "카페/디저트"
    SHOPPING = "쇼핑"
    LIFE = "생활"
    TRANSPORT = "교통"
    ETC = "기타"

# class StoreType(str, Enum):
#     RESTAURANT = "음식점"
#     CAFE = "카페"
#     RETAIL = "소매점"
#     SERVICE = "서비스"
#
# class ItemCheckRule(str, Enum):
#     ALWAYS = "항상 필요"
#     NEVER = "필요없음"
#     CONDITIONAL = "조건부"