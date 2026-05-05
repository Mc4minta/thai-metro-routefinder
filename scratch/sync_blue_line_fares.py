import csv
import json
import os

# Paths
fix_file = 'src/main/resources/fix-fare/price_blue_line_fix.csv'
formatted_file = 'src/main/resources/fare/price_matrices_formatted.csv'
data_json = 'src/main/resources/data.json'

def load_fix_fares():
    fares = {}
    with open(fix_file, mode='r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        stations = [s.strip() for s in header[1:]]
        for row in reader:
            if not row: continue
            from_st = row[0].strip()
            for i, price in enumerate(row[1:]):
                to_st = stations[i]
                fares[(from_st, to_st)] = price.strip()
    return fares, stations

def update_formatted_csv(fares, stations):
    # Standard order for formatted csv is BL01, BL02, ..., BL38
    std_stations = [f'BL{i:02d}' for i in range(1, 39)]
    
    with open(formatted_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    new_lines = []
    in_blue_line = False
    header_found = False
    
    for line in lines:
        if '**BLUE LINE**' in line:
            new_lines.append(line)
            in_blue_line = True
            continue
        
        if in_blue_line:
            if line.startswith('**') or line.strip() == '':
                in_blue_line = False
                new_lines.append(line)
                continue
            
            if not header_found:
                # Keep the header as is or reconstruct
                new_lines.append('จาก \\ ไป,' + ','.join(std_stations) + '\n')
                header_found = True
                continue
            
            # Row data
            parts = line.strip().split(',')
            from_st = parts[0]
            if from_st in std_stations:
                row_prices = []
                for to_st in std_stations:
                    price = fares.get((from_st, to_st), fares.get((to_st, from_st), '0'))
                    row_prices.append(price)
                new_lines.append(from_st + ',' + ','.join(row_prices) + '\n')
            else:
                new_lines.append(line)
        else:
            new_lines.append(line)
            
    with open(formatted_file, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)

def update_data_json(fares):
    with open(data_json, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    updated_count = 0
    for edge in data['edges']:
        if edge.get('line') == 'BL' and edge.get('type') == 'fare':
            from_st = edge['from']
            to_st = edge['to']
            if (from_st, to_st) in fares:
                edge['price'] = float(fares[(from_st, to_st)])
                updated_count += 1
            elif (to_st, from_st) in fares:
                # If asymmetric data is not provided, use symmetric?
                # The fix file seems to have all pairs though.
                edge['price'] = float(fares[(to_st, from_st)])
                updated_count += 1
                
    with open(data_json, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    print(f'Updated {updated_count} edges in data.json')

if __name__ == '__main__':
    fares, stations = load_fix_fares()
    update_formatted_csv(fares, stations)
    update_data_json(fares)
    print('Sync complete.')
