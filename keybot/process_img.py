import cv2, sys, os, shutil, json
from tqdm import tqdm

src_dir, des_dir = sys.argv[1:]
print("compile picture: src = " + src_dir + ", des_dir = " + des_dir)
if os.path.isdir(des_dir):
    shutil.rmtree(des_dir)
os.makedirs(des_dir)

answer_map = []

for name in tqdm(os.listdir(src_dir)):
    input_file = os.path.join(src_dir, name)
    img = cv2.imread(input_file)
    # add some noise
    strip = 10
    height = 1
    offset = 1
    color = 0
#     for h in range((img.shape[0]) // (strip + height)):
#         y1 = (strip + height) * h + offset
#         y2 = y1 + height
#         img[y1:y2, 0:img.shape[1]-1] = (color, color, color)
#     for h in range((img.shape[1]) // (strip + height)):
#         y1 = (strip + height) * h + offset
#         y2 = y1 + height
#         # img[y1:y2, 0:img.shape[1]-1] = (0, 0, 0)
#         img[0:img.shape[0]-1, y1:y2] = (color, color, color)

    new_name = name.replace(".png", ".bmp")
    new_name, answer = new_name.split(" ", 1)
    extension = answer[-4:]
    answer = answer[:-4]
    new_name = new_name + extension
    _double = None
    _list = None
    _type = "text"
    _text = answer
    if 'div' in answer:
        _list = answer.split("div")
        _double = float(_list[0]) / float(_list[1])
        _text = str(_list[0]) + "/" + str(_list[1])
        _type = 'fraction'
    elif ' ' in answer:
        _list = answer.split(" ")
        _text = ",".join(_list)
        _type = "list"
    elif answer.isnumeric():
        _double = float(answer)
        _type = 'double'
        _text = str(_double)
    elif 'or' in answer:
        _list = answer.split("or")
        _text = "或".join(_list)
        _type = "or"

    answer_map.append({
        "file": new_name,
        "text": _text,
        "type": _type,
        "double": _double,
        "list": _list
    })
    cv2.imwrite(os.path.join(des_dir, new_name), img)

print(answer_map)
with open(os.path.join(des_dir, "answer.json"), "w") as f:
    json.dump(answer_map, f)