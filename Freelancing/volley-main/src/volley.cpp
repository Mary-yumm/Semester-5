/**
 * @file volley.cpp
 * @brief Implementation of the Volley class for managing the game logic of a volleyball simulation.
 */

#include "volley.hpp"
#include "constants.hpp"
#include "utils.hpp"
#include "assets.hpp"
#include <X11/Xlib.h>
#include <thread>
#include <iostream>
#include <random>

namespace vl
{
  /**
   * @brief Constructs a Volley game object.
   * @param isTwoVsTwo Indicates if the game mode is two players vs two players.
   */

  Volley::Volley(bool isTwoVsTwo) : pauseMenu(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT)
  {
    this->scoreUpdated = true;      ///< Indicates whether the score has been updated.
    this->gameEnded = false;        ///< Indicates whether the game has ended.
    this->pause = false;            ///< Indicates if the game is paused.
    this->latest_score = -1;        ///< Stores the latest score.
    this->TeamPlayerServe1 = false; ///< Indicates if team 1 has the serve.
    this->TeamPlayerServe2 = false; ///< Indicates if team 2 has the serve.

    // Set the game mode.
    this->isTwoVsTwo = isTwoVsTwo;

    // Create SFML window
    _window = new sf::RenderWindow(sf::VideoMode(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT), VL_APP_TITLE);
    _window->setActive(false);
    _window->setFramerateLimit(VL_FPS);

    // Initialize players based on the mode
    if (isTwoVsTwo)
    {
      _players[0] = new vl::Character(VL_ASSET_IMG_PLAYER1, sf::Vector2f(VL_WINDOW_WIDTH / 4, 660), VL_PLAYER_FRICTION);
      _players[1] = new vl::Character(VL_ASSET_IMG_PLAYER2, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600), VL_PLAYER_FRICTION);
      _players[2] = new vl::Character(VL_ASSET_IMG_PLAYER3, sf::Vector2f(VL_WINDOW_WIDTH / 4 - 150, 660), VL_PLAYER_FRICTION);
      _players[3] = new vl::Character(VL_ASSET_IMG_PLAYER4, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 150, 600), VL_PLAYER_FRICTION);

      // Set playable areas for players.
      _players[0]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[1]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[2]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[3]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    }
    else
    {
      _players[0] = new vl::Character(VL_ASSET_IMG_PLAYER1, sf::Vector2f(VL_WINDOW_WIDTH / 4, 660), VL_PLAYER_FRICTION);
      _players[1] = new vl::Character(VL_ASSET_IMG_PLAYER2, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600), VL_PLAYER_FRICTION);

      // Set playable areas for players.
      _players[0]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[1]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    }

    // Create ball sprite and set its properties.
    _ball = new vl::Ball(VL_ASSET_IMG_BALL, sf::Vector2f(5 * VL_WINDOW_WIDTH / 8, 200), VL_BALL_FRICTION, _lastPlayer);
    _ball->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH - VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    _ball->setObserver(this);

    // Initialize score value
    _score = new Score();
    _scores[0] = 0u;  ///< Score of team/player 1.
    _scores[1] = 0u;  ///< Score of team/player 2.
    _lastPlayer = 0u; ///< Tracks the last player who touched the ball.

    // Create shadows
    if (isTwoVsTwo)
    {
      for (auto &shadow : _shadows)
      {
        shadow = new sf::CircleShape(VL_SHADOW_WIDTH / 2, 10u);
        shadow->setFillColor(sf::Color(200u, 200u, 200u));
        shadow->setOrigin(VL_SHADOW_WIDTH / 2, 0u);
        shadow->setScale(1.0f, 0.3f);
      }
    }
    else
    {
      for (int i = 0; i < 3; i++)
      {
        _shadows[i] = new sf::CircleShape(VL_SHADOW_WIDTH / 2, 10u);
        _shadows[i]->setFillColor(sf::Color(200u, 200u, 200u));
        _shadows[i]->setOrigin(VL_SHADOW_WIDTH / 2, 0u);
        _shadows[i]->setScale(1.0f, 0.3f);
      }
    }

    // Create non-playable scene objects.
    _sceneObjects[0] = new vl::Entity(VL_ASSET_IMG_BEACH, sf::Vector2f(0.0f, 0.0f));
    _sceneObjects[1] = new vl::Entity(VL_ASSET_IMG_TREE, sf::Vector2f(80.0f, 250.0f));
    _sceneObjects[2] = new vl::Entity(VL_ASSET_IMG_NET, sf::Vector2f(450.0f, 415.0f));

    // Load sounds.
    _sounds[0] = new vl::Sound(VL_ASSET_SND_LOSE);
    _sounds[1] = new vl::Sound(VL_ASSET_SND_BOUNCE);

    // Load a font (ensure the font file is accessible in the assets directory)
    if (!font.loadFromFile("arial.ttf"))
    {
      std::cerr << "Error loading font file!" << std::endl;
      exit(EXIT_FAILURE); // Exit if font loading fails
    }
    // Initialize the "Hit 1 to start" message
    startMessage.setFont(font);                  // Set the font
    startMessage.setString("Hit 1 to start");    // Message text
    startMessage.setCharacterSize(50);           // Font size
    startMessage.setFillColor(sf::Color::White); // Text color
    startMessage.setStyle(sf::Text::Bold);       // Optional: Make it bold
    startMessage.setPosition(
        VL_WINDOW_WIDTH / 2 - 150, // X position (centered)
        VL_WINDOW_HEIGHT / 2 - 50  // Y position
    );

    player1Message.setFont(font);
    player1Message.setString("Player/Team 1");
    player1Message.setCharacterSize(20);
    player1Message.setFillColor(sf::Color::White);
    player1Message.setStyle(sf::Text::Bold);
    player1Message.setPosition(
        VL_WINDOW_WIDTH / 2 - 350,
        VL_WINDOW_HEIGHT / 2 - 250);

    player2Message.setFont(font);
    player2Message.setString("Player/Team 2");
    player2Message.setCharacterSize(20);
    player2Message.setFillColor(sf::Color::White);
    player2Message.setStyle(sf::Text::Bold);
    player2Message.setPosition(
        VL_WINDOW_WIDTH / 2 + 250,
        VL_WINDOW_HEIGHT / 2 - 250);

    // win messages.
    winMessage.setFont(font);
    winMessage.setString(getWinner() == 0 ? "Team/Player 1 Wins!" : "Team/Player 2 Wins!");
    winMessage.setCharacterSize(50);
    winMessage.setFillColor(sf::Color::White);
    winMessage.setStyle(sf::Text::Bold);

    sf::FloatRect winBounds = winMessage.getGlobalBounds();
    winMessage.setPosition(
        (_window->getSize().x - winBounds.width) / 2,
        (_window->getSize().y - winBounds.height) / 3);

    returnMessage.setFont(font);
    returnMessage.setString("Press Enter to return to Main Menu");
    returnMessage.setCharacterSize(30);
    returnMessage.setFillColor(sf::Color::White);

    sf::FloatRect returnBounds = returnMessage.getGlobalBounds();
    returnMessage.setPosition(
        (_window->getSize().x - returnBounds.width) / 2,
        (_window->getSize().y - returnBounds.height) / 2);
  }

  /**
   * @brief Destructor for the Volley class. Cleans up dynamically allocated resources.
   */

  Volley::~Volley()
  {
    for (auto element : _sounds)
      delete element;

    for (auto element : _sceneObjects)
      delete element;

    for (auto element : _shadows)
      delete element;

    delete _score;
    delete _ball;

    for (auto element : _players)
      delete element;

    delete _window;
  }

  /**
   * @brief Starts a new game by resetting the scores and positioning the ball.
   */
  void Volley::newGame()
  {
    this->scoreUpdated = true;
    this->gameEnded = false;
    this->pause = false;
    this->latest_score = -1;

    // Initialize and reset the score.
    delete _score;
    _score = new Score();

    _scores[0] = 0u;
    _scores[1] = 0u;
    _lastPlayer = 0u;
    reset();

    // Randomly decide which side serves first.
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dis(0, 1);

    int serve_side = dis(gen);

    if (serve_side == 0)
    {
      // Position the ball for Player 1 (left side)
      _ball->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 + 150, 175)); // Left player's position
    }
    else
    {

      // Position the ball for Player 2 (right side)
      _ball->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 - 150, 175)); // Right player's position
    }

    _ball->handleEvent(vl::Event::LEFT);
  }

  /**
   * @brief Changes the game mode between one vs one and two vs two.
   */
  void Volley::changeGameMode()
  {
    if (!isTwoVsTwo)
    {
      this->isTwoVsTwo = true;

      // Add additional players for two vs two mode.
      _players[2] = new vl::Character(VL_ASSET_IMG_PLAYER3, sf::Vector2f(VL_WINDOW_WIDTH / 4 - 150, 660), VL_PLAYER_FRICTION);
      _players[3] = new vl::Character(VL_ASSET_IMG_PLAYER4, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 150, 600), VL_PLAYER_FRICTION);

      // Set playable areas for the additional players.
      _players[2]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[3]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));

      // Delete existing shadows and recreate them for all players.
      int i = 0;
      for (auto element : _shadows)
      {
        if (i == 3)
          break;
        delete element;
        i++;
      }

      for (auto &shadow : _shadows)
      {
        shadow = new sf::CircleShape(VL_SHADOW_WIDTH / 2, 10u);
        shadow->setFillColor(sf::Color(200u, 200u, 200u));
        shadow->setOrigin(VL_SHADOW_WIDTH / 2, 0u);
        shadow->setScale(1.0f, 0.3f);
      }
    }
    else
    {
      this->isTwoVsTwo = false;
    }
  }

  /**
   * @brief Renders the game objects, UI, and handles display updates.
   */
  void Volley::render()
  {

    _window->clear(); ///< Clears the current window frame.

    if (pause)
    {
      // Render the pause menu if the game is paused.
      pauseMenu.draw(*_window);
    }
    else
    {

      // Draw the background and scene objects.
      _window->draw(_sceneObjects[0]->getSprite()); ///< Draw background.

      if (isTwoVsTwo)
      {
        for (auto &shadow : _shadows)
          _window->draw(*shadow);
      }
      else
      {
        _window->draw(*_shadows[0]);
        _window->draw(*_shadows[1]);
        _window->draw(*_shadows[2]);
      }

      // Render players
      if (isTwoVsTwo)
      {
        // Draw shadows for all players.
        for (auto &player : _players)
          if (player)
            _window->draw(player->getSprite());
      }
      else
      {
        // Draw shadows for two players in one vs one mode.
        _window->draw(_players[0]->getSprite());
        _window->draw(_players[1]->getSprite());
      }

      // Render the ball and additional scene objects.
      _window->draw(_ball->getSprite());
      _window->draw(_sceneObjects[2]->getSprite());
      _window->draw(_sceneObjects[1]->getSprite());
      _window->draw(_score->getSprite());

      // Render player/team labels.
      _window->draw(player1Message);
      _window->draw(player2Message);

      // Display "Hit 1 to start" message during score updates if no one has won.
      if (scoreUpdated && !didSomeoneWin())
      {
        _window->draw(startMessage);
      }
      if (isBallStatic)
        _window->draw(_ball->arrowSprite); // Draw the arrow sprite
    }

    // Display the win message if someone has won.
    if (didSomeoneWin())
    {
      winMessage.setString(getWinner() == 0 ? "Team/Player 1 Wins!" : "Team/Player 2 Wins!");
      _window->draw(winMessage);
      _window->draw(returnMessage);
    }

    // Display the rendered frame.
    _window->display();
  }

  /**
   * @brief Resolves collisions between the ball and players, as well as handling scoring and other game mechanics.
   */
  void Volley::resolveCollisions()
  {
    // Static variables to track collision states for players

    static bool player1Collision = false;
    static bool player2Collision = false;
    static bool player3Collision = false;
    static bool player4Collision = false;
    bool flag = false;

    // Rotate the ball based on its velocity
    auto angle = (_ball->getVelocity().x * 180.0f) / (32.0f * 3.14f);
    _ball->rotate(angle);

    // Handle collision with Player 1
    if (_ball->isCollidingWith(*_players[0]))
    {
      if (!player1Collision)
      {
        _ball->bounce_p1++;
        _ball->bounce_p2 = 0;
        _ball->bounce_p4 = 0;
        if (_ball->bounce_p3 == 0 && _ball->bounce_p1 == 2 && isTwoVsTwo)
        {
          flag = true;
        }
        _ball->bounce(*_players[0]);
        _lastPlayer = 0u;
        _sounds[1]->play();
        player1Collision = true;
      }
    }
    else
    {
      player1Collision = false;
    }

    // Handle collision with Player 2
    if (_ball->isCollidingWith(*_players[1]))
    {
      if (!player2Collision)
      {
        _ball->bounce_p1 = 0;
        _ball->bounce_p2++;
        _ball->bounce_p3 = 0;
        if (_ball->bounce_p4 == 0 && _ball->bounce_p2 == 2 && isTwoVsTwo)
        {
          flag = true;
        }
        _ball->bounce(*_players[1]);
        _lastPlayer = 1u;
        _sounds[1]->play();
        player2Collision = true;
      }
    }
    else
    {
      player2Collision = false;
    }

    // Handle collisions for 2v2 mode

    if (isTwoVsTwo)
    {
      // Handle collision with Player 3
      if (_ball->isCollidingWith(*_players[2]))
      {
        if (!player3Collision)
        {
          _ball->bounce_p3++;
          _ball->bounce_p2 = 0;
          _ball->bounce_p4 = 0;
          if (_ball->bounce_p1 == 0 && _ball->bounce_p3 == 2 && isTwoVsTwo)
          {
            flag = true;
          }

          _ball->bounce(*_players[2]);
          _lastPlayer = 0u;
          _sounds[1]->play();
          player3Collision = true;
        }
      }
      else
      {
        player3Collision = false;
      }
      // Handle collision with Player 4
      if (_ball->isCollidingWith(*_players[3]))
      {
        if (!player4Collision)
        {
          _ball->bounce_p1 = 0;
          _ball->bounce_p3 = 0;
          _ball->bounce_p4++;
          if (_ball->bounce_p2 == 0 && _ball->bounce_p4 == 2 && isTwoVsTwo)
          {
            flag = true;
          }
          _ball->bounce(*_players[3]);
          _lastPlayer = 1u;
          _sounds[1]->play();
          player4Collision = true;
        }
      }
      else
      {
        player4Collision = false;
      }
    }

    // Check for scoring conditions

    // Check if the ball is bounced three times without crossing the net
    if ((_ball->bounce_p1 + _ball->bounce_p3) > 3 || (_ball->bounce_p2 + _ball->bounce_p4) > 3 || flag == true)
    {
      _scores[1 - _lastPlayer]++;
      latest_score = 1 - _lastPlayer;
      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();
      if (1 - _lastPlayer == 0)
      {
        TeamPlayerServe1 = !TeamPlayerServe1;
      }
      else
      {
        TeamPlayerServe2 = !TeamPlayerServe2;
      }
      reset();
      return;
    }

    // Define the bounding box for the net
    const sf::Vector2f p = _sceneObjects[2]->getPosition();
    const sf::Vector2u s = _sceneObjects[2]->getSize();

    sf::FloatRect net_box(sf::Vector2f(p.x + s.x / 2.0f - 10.0f, p.y + 50.0f), sf::Vector2f(20.0f, 300.0f));

    // Check for collision with the net
    if (net_box.contains(_ball->getPosition()))
    {
      _scores[1 - _lastPlayer]++;
      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();
      if (1 - _lastPlayer == 0)
      {
        TeamPlayerServe1 = !TeamPlayerServe1;
      }
      else
      {
        TeamPlayerServe2 = !TeamPlayerServe2;
      }
      reset();
    }
  }

  /**
   * @brief Resets the game to its initial state based on the latest score.
   *
   * This function repositions the ball and players depending on which player is leading,
   * updates the players' positions for the 2v2 mode, resets the bounce counts, and stops
   * the game elements for the next round. It also checks if the game has ended based on
   * the winning score condition.
   */
  void Volley::reset()
  {
    delete _ball;
    _ball = new vl::Ball(VL_ASSET_IMG_BALL, sf::Vector2f(5 * VL_WINDOW_WIDTH / 8, 200), VL_BALL_FRICTION, _lastPlayer);
    _ball->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH - VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    _ball->setObserver(this);

    if (latest_score == 0) // Player 1 (left side) is leading
    {
      _ball->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, VL_WINDOW_HEIGHT / 2 - 75)); // Position on Player 1's side

      _ball->arrowSprite.setPosition(VL_WINDOW_WIDTH / 4,
                                     VL_WINDOW_HEIGHT / 2 + 75); // Adjust position
      _ball->side = 0u;
      if (isTwoVsTwo)
      {
        if (TeamPlayerServe1)
        { // 1
          _players[2]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 660));
          _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 - 150, 660));
        }
        else // 0
        {
          _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 660));
          _players[2]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 - 150, 660));
        }
        _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600));
        _players[3]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 150, 600));
      }
    }
    else if (latest_score == 1) // Player 2 (right side) is leading
    {
      _ball->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, VL_WINDOW_HEIGHT / 2 - 75)); // Position on Player 2's side

      _ball->arrowSprite.setPosition(3 * VL_WINDOW_WIDTH / 4,
                                     VL_WINDOW_HEIGHT / 2 + 75); // Adjust position

      _ball->side = 1u;
      if (isTwoVsTwo)
      {
        if (TeamPlayerServe2) // 1
        {
          _players[3]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600));
          _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 150, 600));
        }
        else // 0
        {
          _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600));
          _players[3]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 150, 600));
        }
        _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 660));
        _players[2]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 - 150, 660));
      }
    }

    _ball->bounce_p1 = 0; // Reset bounce counts
    _ball->bounce_p2 = 0;
    _ball->bounce_p3 = 0;
    _ball->bounce_p4 = 0;

    // Reset player positions for non-2v2 mode

    if (!isTwoVsTwo)
    {
      _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 660));
      _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600));
    }

    // Stop players and ball if 2v2 mode

    if (isTwoVsTwo)
    {
      _players[2]->stop();
      _players[3]->stop();
    }

    _ball->stop();
    _players[0]->stop();
    _players[1]->stop();

    this->scoreUpdated = true;

    // Check if a player has won

    if (_scores[0] >= WINNING_SCORE || _scores[1] >= WINNING_SCORE)
    {
      bool score0 = (_scores[0] > (_scores[1] + 1));
      bool score1 = (_scores[1] > (_scores[0] + 1));
      if (score0 || score1)
      {
        gameEnded = true;
      }
    }
    isBallStatic = true;
    _ball->handleEvent(vl::Event::LEFT);
  }

  /**
   * @brief Updates the game state including player and ball movements,
   *        and the positioning of shadows based on player and ball positions.
   *
   * This function resolves collisions, updates player and ball positions,
   * and adjusts shadow positioning and scaling based on player heights
   * in both 1v1 and 2v2 modes.
   */
  void Volley::update()
  {
    auto dt = gameClock.restart().asSeconds();
    resolveCollisions();

    if (!pause)
    {
      // Update ball if not static
      if (!isBallStatic)
        _ball->update(dt);

      // Update players' positions in 2v2 mode
      if (isTwoVsTwo)
      {
        for (auto &player : _players)
          player->update(dt);
      }
      else
      {
        _players[0]->update(dt);
        _players[1]->update(dt);
      }
    }

    // Update shadows for all players and ball

    if (isTwoVsTwo)
    {
      for (int i = 0; i < VL_NB_PLAYERS; i++)
      {
        if (_players[i])
        {
          _shadows[i]->setPosition(_players[i]->getPosition().x, 690);
          _shadows[i]->setScale(_players[i]->getPosition().y / 700, 0.3 * _players[i]->getPosition().y / 700);
        }
      }
      _shadows[4]->setPosition(_ball->getPosition().x, 690);
      _shadows[4]->setScale(_ball->getPosition().y / 700, 0.3 * _ball->getPosition().y / 700);
    }
    else
    {
      // Update shadows for 1v1 mode

      _shadows[0]->setPosition(_players[0]->getPosition().x, 690);
      _shadows[0]->setScale(_players[0]->getPosition().y / 700, 0.3 * _players[0]->getPosition().y / 700);
      _shadows[1]->setPosition(_players[1]->getPosition().x, 690);
      _shadows[1]->setScale(_players[1]->getPosition().y / 700, 0.3 * _players[1]->getPosition().y / 700);

      _shadows[2]->setPosition(_ball->getPosition().x, 690);
      _shadows[2]->setScale(_ball->getPosition().y / 700, 0.3 * _ball->getPosition().y / 700);
    }
  }

  /**
   * @brief Handles user input and game events, such as key presses for game control and menu navigation.
   *
   * This function processes user inputs through the event loop, handling key presses to
   * control the game or navigate the pause menu. It supports both single-player and
   * two-player modes and handles game-specific actions like pausing, restarting, or quitting.
   */
  void Volley::handleEvents()
  {
    // Start the game loop and process events

    sf::Event event;
    while (_window->pollEvent(event))
    {
      // Handle key presses

      if (event.type == sf::Event::KeyPressed)
      {
        if (pause || scoreUpdated)
        {
          // Handle events when the game is paused or score has been updated

          switch (event.key.code)
          {
          case sf::Keyboard::Num1:
            // Resets the score and display the hit message if not a winning score
            if (scoreUpdated && !didSomeoneWin())
            {
              scoreUpdated = false;
            }
            break;
          case sf::Keyboard::P:
            // Pause the game
            handlePauseEvent();

            break;
          case sf::Keyboard::Up: // Navigate up in the menu
            pauseMenu.moveUp();
            break;
          case sf::Keyboard::Down: // Navigate down in the menu
            pauseMenu.moveDown();
            break;
          case sf::Keyboard::Enter: // Select menu option
            switch (pauseMenu.getSelectedOption())
            {
            case 0: // "Resume Game"
              pause = false;
              break;
            case 1: // "New Game"
              pause = false;
              newGame();
              break;
            case 2: // "Change Game Mode"
              pause = false;
              changeGameMode();
              break;
            case 3:             // "Exit"
              _window->close(); // Close the game
              break;
            }
            break;
          }
        }
        else
        {
          // Handle events during active gameplay

          switch (event.key.code)
          {
          case sf::Keyboard::P:
            // Pause the game
            handlePauseEvent();
            break;
          case sf::Keyboard::W:
            // Player 1 jump event
            if(!isBallStatic)
            _players[0]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::I:
            // Player 2 jump event
            if(!isBallStatic)
            _players[1]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::G:
            // Ball drop event
            if (isBallStatic)
            {
              isBallStatic = false;
              _ball->bounce_serving();
            }

            break;
          case sf::Keyboard::V:
            // Ball move left event
            if (isBallStatic)
            {
              //_ball->rotateLeft();
              _ball->handleEvent(vl::Event::LEFT);
            }
            break;
          case sf::Keyboard::B:
            // Ball move right event
            if (isBallStatic)
            {
              //_ball->rotateRight();
              _ball->handleEvent(vl::Event::RIGHT);
            }
            break;
          case sf::Keyboard::T:
            // Player 3 jump event in two-player mode
            if (isTwoVsTwo && !isBallStatic)
              _players[2]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::Up:
            // Player 4 jump event in two-player mode
            if (isTwoVsTwo && !isBallStatic)
              _players[3]->handleEvent(vl::Event::JUMP);
            break;

          case sf::Keyboard::Escape:
            // Close the window

            _window->close();
            break;

          default:
            break;
          }
        }
      }
      // Handle window close event

      if (event.type == sf::Event::Closed)
        _window->close();
    }

    // Handle real-time key presses during active gameplay

    if (!pause && !scoreUpdated && !isBallStatic)
    {

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::A))
        _players[0]->handleEvent(vl::Event::LEFT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::D))
        _players[0]->handleEvent(vl::Event::RIGHT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::J))
        _players[1]->handleEvent(vl::Event::LEFT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::L))
        _players[1]->handleEvent(vl::Event::RIGHT);

      if (isTwoVsTwo)
      {
        if (sf::Keyboard::isKeyPressed(sf::Keyboard::F))
          _players[2]->handleEvent(vl::Event::LEFT);
        if (sf::Keyboard::isKeyPressed(sf::Keyboard::H))
          _players[2]->handleEvent(vl::Event::RIGHT);

        if (sf::Keyboard::isKeyPressed(sf::Keyboard::Left))
          _players[3]->handleEvent(vl::Event::LEFT);
        if (sf::Keyboard::isKeyPressed(sf::Keyboard::Right))
          _players[3]->handleEvent(vl::Event::RIGHT);
      }
    }

    // Handle win condition and close the window on Enter key press

    if (didSomeoneWin())
    {
      while (true)
      {

        if (sf::Keyboard::isKeyPressed(sf::Keyboard::Enter))
        {
          _window->close();
          break;
        }
      }
    }
  }

  /**
   * @brief Handles the event when the ball falls, updating the score and resetting the game.
   *
   * This function is triggered when the ball falls and determines which team won the point,
   * updates the score, and resets the game state accordingly. It also handles which team
   * should serve next.
   *
   * @param event The event that triggered the ball fall.
   */
  void Volley::onNotify(const vl::Event &event)
  {
    if (event == vl::Event::BALL_FELL)
    {
      // Update score based on which side the ball fell

      if (_ball->getPosition().x < VL_WINDOW_WIDTH / 2)
      {
        _scores[1]++;
        latest_score = 1;
        TeamPlayerServe2 = !TeamPlayerServe2;
      }
      else
      {
        _scores[0]++;
        latest_score = 0;
        TeamPlayerServe1 = !TeamPlayerServe1;
      }

      // Update score display and play sound
      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();

      // Reset the game state

      reset();
    }
  }

  /**
   * @brief Main game loop that handles events, updates the game state, and renders the game.
   *
   * This function runs the main game loop, processing events, updating the game state, and rendering
   * the game each frame. It will continuously loop until the window is closed, at which point it
   * breaks out of the loop and performs necessary cleanup.
   */
  void Volley::run()
  {

    // Game loop
    while (true)
    {
      handleEvents(); ///< Handle user input and game events.
      update();       ///< Update game state (e.g., player movements, physics).
      render();       ///< Render the current game frame.

      // Exit if the window is closed
      if (!_window->isOpen())
      {
        gameEnded = true; ///< Mark the game as ended.
        break;            ///< Exit the game loop.
      }
    }

    // Close the window if it is open

    if (_window->isOpen())
    {
      _window->close(); ///< Close the game window.
    }
  }

  /**
   * @brief Toggles the pause state of the game.
   *
   * This function toggles the `pause` flag, pausing or resuming the game accordingly.
   */
  void Volley::handlePauseEvent()
  {
    pause = !pause; ///< Toggle the pause state.
  }

  /**
   * @brief Checks if the game has ended.
   *
   * This function returns a boolean indicating whether the game has ended, typically due to
   * one team winning.
   *
   * @return True if the game has ended, otherwise false.
   */
  bool Volley::didSomeoneWin()
  {
    return gameEnded; ///< Return the current game-ended state.
  }

  /**
   * @brief Determines the winner of the game.
   *
   * This function returns the index of the winning team based on the current score.
   * It assumes that a team reaches the winning score to be declared the winner.
   *
   * @return The index of the winning team (0 or 1).
   */
  int Volley::getWinner()
  {
    // If team 0 has a score equal to or greater than the winning score, they are the winner
    return (_scores[0] >= WINNING_SCORE) ? 0 : 1;
  }

}
