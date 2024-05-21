# Backend GlobalGuess

## Technologies
This repository uses the [spring framework](https://spring.io/projects/spring-framework). We use the websocket dependency provided by spring, which utilizes the [STOMP](https://stomp.github.io/) protocol for communication.

## High-Level Components
All code not pertaining to project building/deployment is located in the /src directory.

### Users & Players
The application uses an in-memory DB, the only entities stored there are defined in [User](/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java). For management in a game, a [Player](/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Player.java) object is instantiated.

### Server-Client Communication
GlobalGuess' backend provides a REST API and allows for websocket connections to enable server-initiated communication. Communication via websocket connection is done using the STOMP protocoll. Both REST endpoints and STOMP messagemappings are located exclusively in [controller](/src/main/java/ch/uzh/ifi/hase/soprafs24/controller) classes. Our [websocket configuration](/src/main/java/ch/uzh/ifi/hase/soprafs24/api) is set-up for heart-beating and uses the default STOMP message-broker provided by spring.

### Lobby System
The lobby system lives in the [Lobby](/src/main/java/ch/uzh/ifi/hase/soprafs24/entites/Lobby.java) and [LobbyService](/src/main/java/ch/uzh/ifi/hase/soprafs24/services/LobbyService.java) components. The service stores a reference to current lobbies, and manages users joining/leaving and settings updates. It publishes application events that are handled in the [LobbyWebsocketController](/src/main/java/ch/uzh/ifi/hase/soprafs24/controlles/LobbyWebsocketController.java) to update clients with new or changed information. Joining and leaving a lobby is done via REST api, this is to ensure that a full-duplex connection to a lobby is only established once a player has been authorized to join a lobby (i.e. it isn't full or running).

### Game Logic
The central components of GlobalGuess' game logic are [GameService](/src/main/java/ch/uzh/ifi/hase/soprafs24/services/GameService.java) and [Game](/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Game.java). [Answers](/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Answer.java) that players input are checked for correctness by calling answer.checkAnswer(), which in turn calls a [Category](/src/main/java/ch/uzh/ifi/hase/soprafs24/categories) type that uses a respective [APIManagers](/src/main/java/ch/uzh/ifi/hase/soprafs24/api) type to make requests to different external APIs. The [GameService](/src/main/java/ch/uzh/ifi/hase/soprafs24/services/GameService.java) maintains a list of references to current game instances. A game is started via the [GameWebsocketController](/src/main/java/ch/uzh/ifi/hase/soprafs24/controllers/GameWebsocketController.java).

## Launch & Deployment
### Running the Project Locally
To run our app locally follow these steps:
1. Clone both this repository and the [client repository](https://github.com/sopra-fs24-group-23/sopra24_client/).
2. Open two terminals at the server directory, either manually or by using IDE-integrated terminals.
3. With your terminals in the server directory, use the command "./gradlew build" in terminal one, and "./gradlew bootRun" in the other.
4. The server should now be running locally on port 8000, navigate to [https://localhost:8000/](https://localhost:8000/) to verify.
5. Now open a terminal in the client directory, again, either manually or by using an IDE-integrated terminal.
6. Use the command "npm install" to install all necessary dependencies.
7. Once the dependencies have been installed, use the command "npm run dev" to run the client locally on port 3000. This should automatically open a browser window at [https://localhost:3000/](https://localhost:3000/)
8. You're done 🚀 The project should now be running locally. You should be able to set breakpoints in your IDE, that will halt if you trigger them by interacting with your local frontend.

### Running Tests
You can run the test suite either directly from your IDE, or by running "./gradlew build", both should work fine. The latter will provide you with a link in your terminal to a more detailed report on passing/failing tests and thrown exceptions and errors.

### Deploying to Google Cloud
The webapp is automatically deployed to google cloud whenever you push to the **main** branch. This should not require any additional setup on your part.

## Illustrations

## Roadmap

## Authors and acknowledgement
- Franziska Geiger, [fr4n715k4](https://github.com/fr4n715k4)
- Nilaksan Selliah, [nilaksan97](https://github.com/nilaksan97)
- Nils Hegetschweiler, [nilshgt](https://github.com/nilshgt)
- Jonas Krumm, [dedphish](https://github.com/Dedphish)

We thank Stefan Schuler ([Steesch](https://github.com/steesch)) for supporting us as our TA throughout the project.

Further, we thank [royru](https://github.com/royru), [isicu](https://github.com/isicu), [marcoleder](https://github.com/marcoleder), [v4lentin1879](https://github.com/v4lentin1879), [luis-tm](https://github.com/luis-tm) and [SvenRingger](https://github.com/SvenRingger) for creating and providing us with a template upon which we built this project. 

## License
[GNU GPLv3](https://github.com/sopra-fs24-group-23/sopra24_server/blob/main/LICENSE)

GlobalGuess is a game about finding terms with a common starting letter as quickly as possible
Copyright (C) 2024  Franziska Geiger, Nilaksan Selliah, Nils Hegetschweiler, Jonas Krumm

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
