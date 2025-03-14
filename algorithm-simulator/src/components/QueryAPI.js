import BaseAPI, { methodType } from "./BaseAPI";

/**
 * QueryAPI class extends BaseAPI and provides specific methods for interacting with 
 * the robot pathfinding API on a FastAPI backend.
 */
export default class QueryAPI extends BaseAPI {
  
  /**
   * Sends a request to the FastAPI backend to calculate the path for the robot, given 
   * its current state and the obstacles on the grid.
   * 
   * @param {Array} obstacles - An array of obstacles where each obstacle has x, y coordinates and a direction.
   * @param {number} robotX - The x coordinate of the robot.
   * @param {number} robotY - The y coordinate of the robot.
   * @param {number} robotDir - The direction the robot is facing (e.g., 0 for North, 2 for East, etc.).
   * @param {function} callback - A callback function that handles the response from the backend.
   */
  static query(obstacles, robotX, robotY, robotDir, callback) {
    /* Construct the content of the request 
      obstacles: the array of obstacles
      robotX: the x coordinate of the robot
      robotY: the y coordinate of the robot
      robotDir: the direction of the robot
      retrying: whether the robot is retrying
    */
    const content = {
      obstacles: obstacles,
      robot_x: robotX,
      robot_y: robotY,
      robot_dir: robotDir,
      retrying: true,
    };

    // Send the request to the FastAPI backend
    this.JSONRequest("/path", methodType.post, {}, {}, content)
      .then((res) => {
        if (callback) {
          callback({
            data: res,
            error: null,
          });
        }
      })
      .catch((err) => {
        console.log(err);
        if (callback) {
          callback({
            data: null,
            error: err,
          });
        }
      });
  }
}
