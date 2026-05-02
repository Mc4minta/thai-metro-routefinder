import json

with open('src/main/resources/data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

for station in data['stations']:
    if station['id'] == 'CEN':
        if station['line'] == 'DG':
            station['id'] = 'CEN_DG'
        elif station['line'] == 'LG':
            station['id'] = 'CEN_LG'

for edge in data['edges']:
    if edge.get('type') == 'fare':
        if edge['from'] == 'CEN':
            if edge['line'] == 'DG':
                edge['from'] = 'CEN_DG'
            elif edge['line'] == 'LG':
                edge['from'] = 'CEN_LG'
        if edge['to'] == 'CEN':
            if edge['line'] == 'DG':
                edge['to'] = 'CEN_DG'
            elif edge['line'] == 'LG':
                edge['to'] = 'CEN_LG'
    elif edge.get('type') == 'interchange':
        if edge['from'] == 'CEN':
            if edge['from_line'] == 'DG':
                edge['from'] = 'CEN_DG'
            elif edge['from_line'] == 'LG':
                edge['from'] = 'CEN_LG'
        if edge['to'] == 'CEN':
            if edge['to_line'] == 'DG':
                edge['to'] = 'CEN_DG'
            elif edge['to_line'] == 'LG':
                edge['to'] = 'CEN_LG'

with open('src/main/resources/data.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=4)
print('Done updating data.json')
