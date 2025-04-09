import json
from datetime import datetime

# 문자열 가격(예: "30,000원", "69%") → 정수형 숫자
def parse_price(price_str):
    return int(''.join(filter(str.isdigit, price_str.replace(',', ''))))

# 날짜 문자열(예: "10.11") → ISO date 형식
def parse_date(md_str):
    month, day = md_str.split('.')
    return f'2025-{int(month):02d}-{int(day):02d}'  # 연도는 고정

# JSON 파일 로드
with open('products_data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

product_sqls = []
price_sqls = []
product_id_seq = 1

for group in data:
    category = group["category"]
    for product in group["data"]:
        product_id = product_id_seq
        product_id_seq += 1

        # 상품 INSERT SQL 생성 (테이블명: products)
        product_sqls.append(f"""INSERT INTO products (
    product_id, product_name, category, image_url, coupang_url,
    highest_price, current_price, unit_price, discount_rate,
    discount_amount, shipping_type, review_count, review_rating
) VALUES (
    {product_id},
    '{product['product_name'].replace("'", "''")}',
    '{category}',
    '{product['image_url']}',
    '{product['coupang_url']}',
    {parse_price(product['highest_price'])},
    {parse_price(product['current_price'])},
    '{product.get('unit_price', '')}',
    {parse_price(product.get('discount_rate', '0'))},
    {parse_price(product.get('discount_amount', '0'))},
    '{product.get('shipping_type', '')}',
    {int(product['review_count'].replace(',', ''))},
    {float(product['review_rating'])}
);""")

        # 가격 히스토리 INSERT SQL 생성 (테이블명: product_price_history)
        for ph in product['price_history']:
            price_sqls.append(f"""INSERT INTO product_price_history (
    product_id, price_date, highest_price, lowest_price, average_price
) VALUES (
    {product_id},
    '{parse_date(ph['price_date'])}',
    {ph['highest_price']},
    {ph['lowest_price']},
    {product['average_price']}
);""")

# SQL 파일로 저장
with open('insert_products.sql', 'w', encoding='utf-8') as out:
    out.write('\n'.join(product_sqls + price_sqls))

print("✅ insert_products.sql 파일이 생성되었습니다!")
