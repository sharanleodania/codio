package com.battlesnake.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.port;
        import static spark.Spark.post;
        import static spark.Spark.get;

        /**
        * This is a simple Battlesnake server written in Java.
 * 
 * For instructions see
 * https://github.com/BattlesnakeOfficial/starter-snake-java/README.md
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
        String port = System.getProperty("PORT");
        if (port == null) {
            LOG.info("Using default port: {}", port);
            port = "3000";
        } else {
            LOG.info("Found system provided port: {}", port);
        }
        port(Integer.parseInt(port));
        get("/", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the start/end request
         */
        private static final Map<String, String> EMPTY = new HashMap<>();

        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/")) {
                    snakeResponse = index();
                } else if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                    snakeResponse = end(parsedRequest);
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }

                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));

                return snakeResponse;
            } catch (JsonProcessingException e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        /**
         * This method is called everytime your Battlesnake is entered into a game.
         * 
         * Use this method to decide how your Battlesnake is going to look on the board.
         *
         * @return a response back to the engine containing the Battlesnake setup
         *         values.
         */
        public Map<String, String> index() {
            Map<String, String> response = new HashMap<>();
            response.put("apiversion", "1");
            response.put("author", "lordsoftonfa"); // TODO: Your Battlesnake Username
            response.put("color", "#ff0000"); // TODO: Personalize
            response.put("head", "all-seeing"); // TODO: Personalize
            response.put("tail", "nr-booster"); // TODO: Personalize
            return response;
        }

        /**
         * This method is called everytime your Battlesnake is entered into a game.
         * 
         * Use this method to decide how your Battlesnake is going to look on the board.
         *
         * @param startRequest a JSON data map containing the information about the game
         *                     that is about to be played.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> start(JsonNode startRequest) {
            LOG.info("START");
            return EMPTY;
        }

        /**
         * This method is called on every turn of a game. It's how your snake decides
         * where to move.
         * 
         * Use the information in 'moveRequest' to decide your next move. The
         * 'moveRequest' variable can be interacted with as
         * com.fasterxml.jackson.databind.JsonNode, and contains all of the information
         * about the Battlesnake board for each move of the game.
         * 
         * For a full example of 'json', see
         * https://docs.battlesnake.com/references/api/sample-move-request
         *
         * @param moveRequest JsonNode of all Game Board data as received from the
         *                    Battlesnake Engine.
         * @return a Map<String,String> response back to the engine the single move to
         *         make. One of "up", "down", "left" or "right".
         */
        public Map<String, String> move(JsonNode moveRequest) {

            try {
                LOG.info("Data: {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moveRequest));
            } catch (JsonProcessingException e) {
                LOG.error("Error parsing payload", e);
            }

            /*
             * Example how to retrieve data from the request payload:
             * 
             * String gameId = moveRequest.get("game").get("id").asText();
             * 
             * int height = moveRequest.get("board").get("height").asInt();
             * 
             */

            JsonNode head = moveRequest.get("you").get("head");
            int height = moveRequest.get("board").get("height").asInt();
            int width = moveRequest.get("board").get("width").asInt();
            JsonNode body = moveRequest.get("you").get("body");
            //JsonNode body = moveRequest.get("pla").get("body");

            ArrayList<String> possibleMoves = new ArrayList<>(Arrays.asList("up", "down", "left", "right"));

            // Don't allow your Battlesnake to move back in on it's own neck
            avoidMyNeck(head, body, possibleMoves);
            avoidBorders(head, possibleMoves, height, width);
            avoidBody(head, body, possibleMoves);
            avoidOthers(head,possibleMoves, moveRequest);

            int health = moveRequest.get("you").get("health").asInt();
            String foodMove = null;
            if (health < 80) {
                foodMove = moveTowardsFood(head, possibleMoves, moveRequest.get("board").get("food"));
            }

            String move;
            if(foodMove != null && possibleMoves.contains(foodMove)) {
                move = foodMove;
            }else{
                if(possibleMoves.isEmpty()){
                    possibleMoves.add("right");
                }
                final int choice = new Random().nextInt(possibleMoves.size());
                move = possibleMoves.get(choice);
            }
         
            // TODO: Using information from 'moveRequest', don't let your Battlesnake pick a
            // move
            // that would collide with another Battlesnake

            // TODO: Using information from 'moveRequest', make your Battlesnake move
            // towards a
            // piece of food on the board

            // Choose a random direction to move in
            
            
            

            LOG.info("MOVE {}", move);

            Map<String, String> response = new HashMap<>();
            response.put("move", move);
            return response; 
        }

        /**
         * Remove the 'neck' direction from the list of possible moves
         * 
         * @param head          JsonNode of the head position e.g. {"x": 0, "y": 0}
         * @param body          JsonNode of x/y coordinates for every segment of a
         *                      Battlesnake. e.g. [ {"x": 0, "y": 0}, {"x": 1, "y": 0},
         *                      {"x": 2, "y": 0} ]
         * @param possibleMoves ArrayList of String. Moves to pick from.
         */
        public void avoidMyNeck(JsonNode head, JsonNode body, ArrayList<String> possibleMoves) {
            JsonNode neck = body.get(1);
            int x = head.get("x").asInt();
            int y = head.get("y").asInt();

            if (neck.get("x").asInt() < head.get("x").asInt()) {
                possibleMoves.remove("left");
                
            } else if (neck.get("x").asInt() > head.get("x").asInt()) {
                possibleMoves.remove("right");
            } else if (neck.get("y").asInt() < head.get("y").asInt()) {
                possibleMoves.remove("down");
            } else if (neck.get("y").asInt() > head.get("y").asInt()) {
                possibleMoves.remove("up");
            }

            
        }
        
        public void avoidBorders(JsonNode head, ArrayList<String> possibleMoves, int height, int width) {
            
            
            int x = head.get("x").asInt();
            int y = head.get("y").asInt();

            if ( y == 0 ){
                possibleMoves.remove("down");
                
            } 
             if (y == height - 1) {
                possibleMoves.remove("up");
            } 
             if (x == 0) {
                possibleMoves.remove("left");
            }
            if (x == width - 1) {
                possibleMoves.remove("right");
            } 
        }
        public void avoidBody(JsonNode head, JsonNode body, ArrayList<String> possibleMoves) {
           
            int headX = head.get("x").asInt();
            int headY = head.get("y").asInt();
            for(String move : new ArrayList<>(possibleMoves)){
                int nextX = headX;
                int nextY = headY;
                switch(move) {
                    case "up":
                        nextY += 1;
                        break;
                    case "down":
                        nextY -= 1;
                        break;
                    case "left":
                        nextX -= 1;
                        break;
                    case "right":
                        nextX += 1;
                        break;
                }
                for(JsonNode segment : body){
                    int bodyX = segment.get("x").asInt();
                    int bodyY = segment.get("y").asInt();

                    if(nextX == bodyX && nextY == bodyY){
                        possibleMoves.remove(move);
                        break;
                    }
                }
            }
            
        }
        public void avoidOthers(JsonNode head, ArrayList<String> possibleMoves, JsonNode moveRequest) {
            JsonNode snakes = moveRequest.get("board").get("snakes");
            String myId = moveRequest.get("you").get("id").asText();
            int myLength = moveRequest.get("you").get("length").asInt();

                for(JsonNode snake : snakes){
                    if(snake.get("id").asText().equals(myId)) continue;

                    JsonNode otherBody = snake.get("body");
                    avoidBody(head, otherBody, possibleMoves);
                }
                
                    avoidHeadToHead(head, possibleMoves, snakes, myId, myLength);
                
            
            
        }

        public void avoidHeadToHead(JsonNode myHead, ArrayList<String> possibleMoves, JsonNode snakes, String myId, int myLength) {
            int myX = myHead.get("x").asInt();
            int myY = myHead.get("y").asInt();  

            for(JsonNode snake : snakes){
                    if(snake.get("id").asText().equals(myId)) continue;

                    int otherLength = snake.get("length").asInt();
                    if(otherLength < myLength + 1) {
                        continue;
                    }

                    JsonNode otherHead = snake.get("head");
                    int otherX = otherHead.get("x").asInt();
                    int otherY = otherHead.get("y").asInt();

                    for(String move : new ArrayList<>(possibleMoves)) {
                        int nextX = myX;
                        int nextY = myY;
                        switch(move){
                            case "up": 
                                nextY += 1;
                                break;
                            case "down":
                                nextY -= 1;
                                break;
                            case "left":
                                nextX -= 1;
                                break;
                            case "right":
                                nextX += 1;
                                break;
                        }

                        if(Math.abs(nextX - otherX) + Math.abs(nextY - otherY) == 1) {
                            possibleMoves.remove(move);
                        }
                    }
            }
        }

        public String moveTowardsFood(JsonNode head, ArrayList<String> possibleMoves, JsonNode foodlist){
            int headX = head.get("x").asInt();
            int headY = head.get("y").asInt();  

            String bestMove = null;
            int bestDistance = Integer.MAX_VALUE;

            for(JsonNode food : foodlist){
                    

                    
                    int foodX = food.get("x").asInt();
                    int foodY = food.get("y").asInt();

                    int currentDistance = Math.abs(headX - foodX) + Math.abs(headY - foodY);

                    for(String move : new ArrayList<>(possibleMoves)) {
                        int nextX = headX;
                        int nextY = headY;
                        switch(move){
                            case "up": 
                                nextY += 1;
                                break;
                            case "down":
                                nextY -= 1;
                                break;
                            case "left":
                                nextX -= 1;
                                break;
                            case "right":
                                nextX += 1;
                                break;
                        }

                        int newDistance = Math.abs(nextX - foodX) + Math.abs(nextY - foodY);
                        if(newDistance < currentDistance && newDistance < bestDistance) {
                            bestDistance = newDistance;
                            bestMove = move;
                        }
                    }
            }
            return bestMove;
        }
        


        /*
         * This method is called when a game your Battlesnake was in ends.
         * 
         * It is purely for informational purposes, you don't have to make any decisions
         * here.
         *
         * @param endRequest a map containing the JSON sent to this snake. Use this data
         *                   to know which game has ended
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
            LOG.info("END");
            return EMPTY;
        }
    }

}
