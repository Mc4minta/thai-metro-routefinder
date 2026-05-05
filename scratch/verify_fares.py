import csv
import json

def verify_blue_line_fares(csv_path, json_path):
    # Load CSV
    csv_fares = {}
    with open(csv_path, mode='r', encoding='utf-8') as f:
        reader = csv.reader(f)
        header = next(reader)
        stations = [s.strip() for s in header[1:]]
        for row in reader:
            if not row: continue
            from_st = row[0].strip()
            for i, price in enumerate(row[1:]):
                to_st = stations[i]
                csv_fares[(from_st, to_st)] = float(price.strip())

    # Load JSON
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    json_fares = {}
    for edge in data['edges']:
        if edge.get('line') == 'BL' and edge.get('type') == 'fare':
            json_fares[(edge['from'], edge['to'])] = edge['price']

    # Compare
    mismatches = []
    missing_in_json = []
    
    for pair, csv_p in csv_fares.items():
        if pair not in json_fares:
            missing_in_json.append(pair)
        elif abs(json_fares[pair] - csv_p) > 0.01:
            mismatches.append(f"{pair}: CSV={csv_p}, JSON={json_fares[pair]}")
            
    print(f"--- Verification Result ---")
    print(f"Total pairs in CSV: {len(csv_fares)}")
    print(f"Total BL fare pairs in JSON: {len(json_fares)}")
    
    if not mismatches and not missing_in_json:
        print("SUCCESS: All Blue Line fares match perfectly!")
    else:
        if mismatches:
            print(f"Found {len(mismatches)} Mismatches:")
            for m in mismatches[:10]: print(f"  {m}")
        if missing_in_json:
            print(f"Found {len(missing_in_json)} pairs missing in JSON:")
            for m in missing_in_json[:10]: print(f"  {m}")

if __name__ == '__main__':
    verify_blue_line_fares(
        'src/main/resources/fix-fare/price_blue_line_fix.csv',
        'src/main/resources/data.json'
    )
