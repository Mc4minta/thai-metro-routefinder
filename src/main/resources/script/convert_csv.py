import json
import csv
import os
import re

# Load data.json
with open('c:/Users/admin/Documents/KMUTT/routefinder/src/main/resources/data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Create a mapping of (name_th, line) -> id
name_to_id = {}
for station in data['stations']:
    name_to_id[(station['name_th'], station['line'])] = station['id']

def clean_name(name):
    # Remove "AMBIGUOUS:" prefix if any
    name = re.sub(r'^AMBIGUOUS:', '', name.strip())
    # Remove existing IDs if any (e.g. "PP01 คลองบางไผ่" -> "คลองบางไผ่")
    name = re.sub(r'^[A-Z0-9]+\s+', '', name)
    return name.strip()

def get_id(name, line_hint=None):
    name = clean_name(name)
    if not name: return ""
    
    # Try exact match with hint
    if line_hint and (name, line_hint) in name_to_id:
        return name_to_id[(name, line_hint)]
    
    # Try matching with line_hint as a prefix of the ID
    matches = []
    for (n, l), id in name_to_id.items():
        if n == name:
            if line_hint and (l == line_hint or id.startswith(line_hint)):
                return id
            matches.append(id)
            
    if len(matches) == 1:
        return matches[0]
    elif len(matches) > 1:
        return f"AMBIGUOUS:{name}"
    return name

fare_dir = 'c:/Users/admin/Documents/KMUTT/routefinder/src/main/resources/fare/'
files_to_process = [
    ('price_arl.csv', 'A'),
    ('price_blue_line.csv', 'BL'),
    ('price_bts_lightgreen_plus_darkgreen.csv', 'LG'), 
    ('price_darkred.csv', 'RN'),
    ('price_pink.csv', 'PK'),
    ('price_purple.csv', 'PP'),
    ('price_yellow.csv', 'YL'),
    ('price_matrices_formatted.csv', None)
]

# Specific line hints for sections in price_matrices_formatted.csv
section_hints = {
    '**DARKRED**': 'RN',
    '**ARL**': 'A',
    '**PURPLE**': 'PP',
    '**BLUE LINE**': 'BL',
    '**BTS lightgreen + darkgreen**': 'LG',
    '**YELLOW**': 'YL',
    '**PINK**': 'PK'
}

for filename, default_hint in files_to_process:
    path = os.path.join(fare_dir, filename)
    if not os.path.exists(path): continue
    
    print(f"Processing {filename}...")
    output_lines = []
    current_hint = default_hint
    
    with open(path, 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        rows = list(reader)
        
        for row in rows:
            if not row:
                output_lines.append(row)
                continue
                
            # Check for section header in master file
            if filename == 'price_matrices_formatted.csv' and len(row) == 1:
                header_text = row[0].strip()
                if header_text in section_hints:
                    current_hint = section_hints[header_text]
                output_lines.append(row)
                continue

            # Check if this is a header row (starts with "จาก \ ไป")
            if row[0].strip() in [r"จาก \\ ไป", r"จาก \ ไป", "Price"]:
                new_row = [row[0]]
                for cell in row[1:]:
                    new_row.append(get_id(cell, current_hint))
                output_lines.append(new_row)
            else:
                # Data row - first column is station
                new_row = [get_id(row[0], current_hint)]
                new_row.extend(row[1:])
                output_lines.append(new_row)
            
    # Write back
    with open(path, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerows(output_lines)

print("Done.")