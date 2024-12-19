/* MIT License

Copyright (c) 2022 Pierre Lefebvre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

#ifndef VOLLEY_HPP
#define VOLLEY_HPP

#include <array>
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include "constants.hpp"
#include "entity.hpp"
#include "character.hpp"
#include "ball.hpp"
#include "observer.hpp"
#include "score.hpp"
#include "sound.hpp"
#include "pauseMenu.hpp"

namespace vl
{
  /**
   * @class Volley
   * @brief This class represents the main game logic for the Volley game.
   *
   * The Volley class manages game state, player actions, collisions, scorekeeping,
   * and renders the game to the screen. It also handles user input, game events,
   * and switches between game modes.
   */
  class Volley : public IObserver
  {
  public:
    /**
     * @brief Constructor for the Volley game.
     *
     * Initializes the game state, sets up necessary game objects and variables.
     *
     * @param isTwoVsTwo Flag indicating whether the game is a 2v2 mode.
     */
    Volley(bool isTwoVsTwo);

    /**
     * @brief Destructor for the Volley game.
     *
     * Cleans up any dynamically allocated resources.
     */
    ~Volley();

    /**
     * @brief Launches the application and starts the game loop.
     *
     * This function runs the main game loop, handling events, updating the game state,
     * and rendering the game until the game is closed.
     */
    void run();

    /**
     * @brief Renders game sprites to the window.
     *
     * This function is responsible for rendering all the graphical objects (e.g., players,
     * ball, backgrounds) onto the game window.
     */
    void render();

    /**
     * @brief Updates the game state.
     *
     * This function updates the positions and statuses of players, the ball, and other game
     * elements, and also handles physics and collision resolution.
     */
    void update();

    /**
     * @brief Event notification handler for Observer pattern.
     *
     * This function handles game events and takes appropriate actions based on the event type.
     *
     * @param event The event that triggered the notification.
     */
    void onNotify(const vl::Event &event);

    /**
     * @brief Checks if someone has won the game.
     *
     * This function returns a boolean indicating whether the game has ended due to a winner.
     *
     * @return True if the game has ended with a winner, otherwise false.
     */
    bool didSomeoneWin();

    /**
     * @brief Gets the winner of the game.
     *
     * This function returns the index of the winning team based on the scores.
     *
     * @return The index (0 or 1) of the winning team.
     */
    int getWinner();

    /**
     * @brief Changes the game mode (e.g., switching between 1v1 and 2v2).
     *
     * This function handles switching between different game modes.
     */
    void changeGameMode();

    /**
     * @brief Starts a new game.
     *
     * This function resets all game elements and starts a new game session.
     */
    void newGame();

  private:
    /**
     * @brief Handles player and game events such as input or actions.
     *
     * This function processes all user input events, such as key presses and releases.
     */
    void handleEvents();
    /**
     * @brief Resolves collisions between game objects (e.g., ball and players).
     *
     * This function checks and handles any collisions in the game, adjusting positions or states as needed.
     */
    void resolveCollisions();
    /**
     * @brief Resolves gravity effects on game objects, especially the ball.
     *
     * This function simulates gravity, applying forces to the ball and potentially other objects.
     *
     * @param dt The time step between updates, used for calculating gravity effects.
     */
    void resolveGravity(double dt);
    /**
     * @brief Resets the game state to start a new round or game.
     *
     * This function resets the position of players, ball, and other relevant game objects.
     */
    void reset();

    std::array<vl::Character *, VL_NB_PLAYERS> _players;       ///< Array of player characters.
    std::array<sf::CircleShape *, VL_NB_SHADOWS> _shadows;     ///< Array of shadow shapes for players.
    std::array<vl::Entity *, VL_NB_NP_ENTITIES> _sceneObjects; ///< Array of non-player entities.
    std::array<vl::Sound *, VL_NB_SOUNDS> _sounds;             ///< Array of sounds used in the game.
    vl::Ball *_ball;                                           ///< Pointer to the ball object.
    sf::RenderWindow *_window;                                 ///< Pointer to the SFML window.
    unsigned int _lastPlayer;                                  ///< Keeps track of the last player who interacted with the game.
    unsigned int _scores[2];                                   ///< Array of scores for two teams.
    Score *_score;                                             ///< Score object to manage the score display.
    void handlePauseEvent();                                   ///< Handles pausing and unpausing the game.
    bool pause;                                                ///< Flag indicating whether the game is paused.
    bool scoreUpdated;                                         ///< Flag indicating whether the score has been updated.
    sf::Font font;                                             ///< Font for displaying text in the game.
    sf::Text startMessage;                                     ///< Text object for the "Press 1 to start" message.
    int WINNING_SCORE = 21;                                    ///< The score required to win the game.
    sf::Clock gameClock;                                       ///< Clock for managing game timing.
    int latest_score;                                          ///< The team/player with most recent score.
    vl::PauseMenu pauseMenu;                                   ///< Instance of the pause menu.
    sf::Text player1Message;                                   ///< Text message for player 1.
    sf::Text player2Message;                                   ///< Text message for player 2.
    sf::Text winMessage;                                       ///< Text message indicating who won.
    sf::Text returnMessage;                                    ///< Text message displayed when returning to the main menu.
    bool isBallStatic = true;                                  ///< Flag indicating whether the ball is static.
    bool isTwoVsTwo;                                           ///< Flag indicating if the game is in 2v2 mode.
    bool gameEnded;                                            ///< Flag indicating whether the game has ended.
    bool TeamPlayerServe1;                                     ///< Flag indicating whether player 1 is serving.
    bool TeamPlayerServe2;                                     ///< Flag indicating whether player 2 is serving.
  };
}

#endif
