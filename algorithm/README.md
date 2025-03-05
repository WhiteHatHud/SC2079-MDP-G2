# Algo Repo

## Setup

```
pip install -r requirements.txt
```

Start the server by

```
python main.py
```

The server will be running at `http://localhost:8000/`

Access the endpoints with `http://localhost:8000/docs`

## Algorithm Request & Reponses

### POST Request to /path:

```
{
    "obstacles": 
        [{"x": 12, "y": 15, "id": 1, "d": 4}, {"x": 7, "y": 9, "id": 2, "d": 4}, {"x": 17, "y": 4, "id": 3, "d": 4}, {"x": 4, "y": 17, "id": 4, "d": 4}, 
        {"x": 19, "y": 9, "id": 5, "d": 4}, {"x": 10, "y": 1, "id": 6, "d": 4}, {"x": 2, "y": 5, "id": 7, "d": 4}, {"x": 11, "y": 11, "id": 8, "d": 4}, {"x": 15, "y": 15, "id": 9, "d": 4}, {"x": 13, "y": 13, "id": 10, "d": 4}], 
    "retrying": true, 
    "robot_x": 1, 
    "robot_y": 1, 
    "robot_dir": 0, 
    "big_turn": 0
}
```

#### Retrying

```
When True, algorithm attempts to use more relaxed constraints (e.g. reducing penalties for closeness to obstacles)
```

#### Big Turn

```
0: Performs a 3,1 turn
1: performs a 4,2 turn
```

### Sample JSON response:

```
{
    "data": {
        "commands": [
            "FR00",
            "FW10",
            "SNAP1",
            "FR00",
            "BW50",
            "FL00",
            "FW60",
            "SNAP2",
            ...,
            "FIN"
        ],
        "distance": 46.0,
        "path": [
            {
                "d": 0,
                "s": -1,
                "x": 1,
                "y": 1
            },
            {
                "d": 2,
                "s": -1,
                "x": 5,
                "y": 3
            },
            ...,
            {
                "d": 2,
                "s": -1,
                "x": 6,
                "y": 9
            },
        ]
    },
    "error": null
}
```

#### Direction (d)

```
NORTH = 0
EAST = 2
SOUTH = 4
WEST = 6
SKIP = 8
```

#### Screenshot (s):

```
Refers to the id of the screenshot taken, i.e. order of obstacles visited. 
Eg s = 7 means the 7th obstacle is visited first.
```

#### Robot Commands

```
RS00 - Gyro Reset - Reset the gyro before starting movement 
FWxx - Forward - Robot moves forward by xx units 
FR00 - Forward Right - Robot moves forward right by 3x1 squares 
FL00 - Forward Left - Robot moves forward left by 3x1 squares 
BWxx - Backward - Robot moves backward by xx units 
BR00 - Backward Right - Robot moves backward right by 3x1 squares 
BL00 - Backward Left - Robot moves backward left by 3x1 squares

SNAPX1_X2 - Snapshot - Robot takes a snapshot (X1: Obstacle id, X2: Centre, Left, Right)
```
