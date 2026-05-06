import json

with open('src/main/resources/data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

print("Edges from BL01:")
for edge in data['edges']:
    if edge.get('from') == 'BL01':
        print(json.dumps(edge, indent=4))

print("\nEdges to BL01:")
for edge in data['edges']:
    if edge.get('to') == 'BL01':
        print(json.dumps(edge, indent=4))

print("\nDiscounts:")
print(json.dumps(data.get('discounts'), indent=4))
