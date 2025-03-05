import requests

# url = "http://192.168.2.13:8000/path"
url = "http://localhost:8000/path"
data = {
    "obstacles": [
        {"d": 2, "id": 4, "x": 5, "y": 10},
        {"d": 4, "id": 2, "x": 10, "y": 19},
        {"d": 6, "id": 5, "x": 19, "y": 2}
   ], 
    "retrying": True, 
    "robot_x": 1, 
    "robot_y": 1, 
    "robot_dir": 0, 
    "big_turn": 1
}

response = requests.post(url, json=data)
print(response.json())