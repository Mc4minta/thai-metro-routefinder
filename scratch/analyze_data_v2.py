import json

with open('src/main/resources/data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

print("--- Edges from BL01 ---")
for edge in data['edges']:
    if edge.get('from') == 'BL01' and edge.get('to') == 'BL03':
        print(json.dumps(edge, indent=4))

print("\n--- Interchange edges involving BL01 ---")
for edge in data['edges']:
    if edge.get('type') == 'interchange' and (edge.get('from') == 'BL01' or edge.get('to') == 'BL01'):
        print(json.dumps(edge, indent=4))
