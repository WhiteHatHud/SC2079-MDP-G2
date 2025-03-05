import time
from utils.pathFinder import PathFinder
#from utils.imageRec import *
from fastapi import FastAPI, Request, HTTPException, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from utils.helper import commandGenerator
import os

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Model for input data validation
class PathFindingRequest(BaseModel):
    obstacles: list
    retrying: bool
    robot_x: int
    robot_y: int
    robot_dir: int
    big_turn: int

#model = loadModel()

@app.get("/")
def readRoot():
    return {"Backend": "Running"}

@app.get("/status")
async def status():
    """
    This is a health check endpoint to check if the server is running
    :return: a json object with a key "result" and value "ok"
    """
    return {"result": "ok"}


@app.post("/path")
async def pathFinding(request: PathFindingRequest):
    """
    This is the main endpoint for the path finding algorithm
    :return: a json object with a key "data" and value a dictionary with keys "distance", "path", and "commands"
    """
    content = request.model_dump()

    # Extract data from request
    obstacles = content['obstacles']
    big_turn = int(content['big_turn'])
    retrying = content['retrying']
    robot_x, robot_y = content['robot_x'], content['robot_y']
    robot_direction = int(content['robot_dir'])

    # Initialize PathFinder object with robot size of 20x20, bottom left corner of robot at (1,1), facing north, and whether to use a big turn or not.
    maze_solver = PathFinder(20, 20, robot_x, robot_y, robot_direction, big_turn=None)

    # Add each obstacle into the PathFinder. Each obstacle is defined by its x,y positions, its direction, and its id
    for ob in obstacles:
        maze_solver.add_obstacle(ob['x'], ob['y'], ob['d'], ob['id'])


    # print obstacle locations
    print("Obstacle locations:")
    for ob in obstacles:
        print(str(ob).replace("'", '"'))


    start = time.time()
   
    optimal_path, distance, optimal_obstacle_order = maze_solver.get_optimal_order_dp(retrying=retrying)
    print(f"Time taken to find shortest path using A* search: {time.time() - start}s")
    print(f"Distance to travel: {distance} units")

    # print("Optimal path:")
    # for i in range(len(optimal_path)):
    #     print(optimal_path[i].get_dict())

    # print("Optimal obstacle order:")
    # for i in range(len(optimal_obstacle_order)):
    #     print(optimal_obstacle_order[i])
    
    # Based on the shortest path, generate commands for the robot
    commands = commandGenerator(optimal_path, obstacles)

    # print commands
    print("Commands:")
    print(commands)

    # Get the starting location and add it to path_results
    path_results = [optimal_path[0].get_dict()]
    # Process each command individually and append the location the robot should be after executing that command to path_results
    i = 0
    for command in commands:
        if command.startswith("SNAP"):
            continue
        if command.startswith("FIN"):
            continue
        elif command.startswith("FW") or command.startswith("FS"):
            i += int(command[2:]) // 10
        elif command.startswith("BW") or command.startswith("BS"):
            i += int(command[2:]) // 10
        else:
            i += 1
        path_results.append(optimal_path[i].get_dict())
    
    return {
        "data": {
            'distance': distance,
            'path': path_results,
            'commands': commands,
            'obstacle_order': optimal_obstacle_order
        },
        "error": None
    }

# @app.post("/image")
# async def predictImage(file: UploadFile = File(...)):
#     """
#     Main endpoint for running image prediction model and saving it.
#     :return: a json object with a key "result" and a dictionary with keys "obstacle_id" and "image_id"
#     """
#     filename = file.filename
#     file_location = os.path.join('uploads', filename)

#     # save the file
#     with open(file_location, "wb") as buffer:
#         buffer.write(await file.read())
    
#     # MAY NEED TO CHANGE DEPENDING ON FILE NAME AFTER RPI SCREENSHOT
#     constituents = filename.split("_")
#     obstacle_id = constituents[1]

#     image_id = predictImage(filename)

#     return {
#         "obstacle_id": obstacle_id,
#         "image_id": image_id
#         }

# @app.get("/stitch")
# async def stitch():
#     img = stitchImage()
#     img.show()
#     return {"result": "ok"}

if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app, host="localhost", port=8000)
    # uvicorn.run(app, host="192.168.2.13", port=8000)

# # '{"obstacles": [{"x": 12, "y": 15, "id": 1, "d": 4}, {"x": 7, "y": 9, "id"\: 2, "d": 4}, {"x": 17, "y": 4, "id": 3, "d": 4}, {"x": 4, "y": 17, "id": 4, "d": 4}, {"x": 19, "y": 9, "id": 5, "d": 4}, {"x": 10, "y": 1, "id": 6, "d": 4}, {"x": 2, "y": 5, "id": 7, "d": 4}, {"x": 11, "y": 11, "id": 8, "d": 4}, {"x": 15, "y": 15, "id": 9, "d": 4}, {"x": 13, "y": 13, "id": 10, "d": 4}], "retrying": true, "robot_x": 1, "robot_y": 1, "robot_dir": 0, "big_turn": 0}'