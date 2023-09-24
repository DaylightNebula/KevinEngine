keycodes = open('jskeycodes.txt', 'r')
output = open('jskeycodes_converted.txt', 'w')

for line in keycodes.readlines():
    tokens = line.split("\t")
    char_id = int(tokens[0].strip(), 16)
    js_id = tokens[1].replace('\"', '').strip()
    if (js_id != "Unidentified"): print("\"" + js_id + "\" -> " + str(char_id), file = output)
