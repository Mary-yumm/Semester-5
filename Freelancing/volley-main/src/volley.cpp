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

  Volley::Volley(bool isTwoVsTwo) : pauseMenu(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT)
  {
    this->scoreUpdated = true;
    this->gameEnded = false;
    this->pause = false;
    this->latest_score = -1;

    // XInitThreads();
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
      _players[2] = new vl::Character(VL_ASSET_IMG_PLAYER3, sf::Vector2f(VL_WINDOW_WIDTH / 4 - 100, 660), VL_PLAYER_FRICTION);
      _players[3] = new vl::Character(VL_ASSET_IMG_PLAYER4, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 100, 600), VL_PLAYER_FRICTION);

      _players[0]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[1]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[2]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[3]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    }
    else
    {
      _players[0] = new vl::Character(VL_ASSET_IMG_PLAYER1, sf::Vector2f(VL_WINDOW_WIDTH / 4, 660), VL_PLAYER_FRICTION);
      _players[1] = new vl::Character(VL_ASSET_IMG_PLAYER2, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600), VL_PLAYER_FRICTION);

      _players[0]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[1]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    }

    // Create ball sprite
    _ball = new vl::Ball(VL_ASSET_IMG_BALL, sf::Vector2f(5 * VL_WINDOW_WIDTH / 8, 200), VL_BALL_FRICTION);
    _ball->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH - VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
    _ball->setObserver(this);

    // Initialize score value
    _score = new Score();
    _scores[0] = 0u;
    _scores[1] = 0u;
    _lastPlayer = 0u;

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

    // Create other NP objects
    _sceneObjects[0] = new vl::Entity(VL_ASSET_IMG_BEACH, sf::Vector2f(0.0f, 0.0f));
    _sceneObjects[1] = new vl::Entity(VL_ASSET_IMG_TREE, sf::Vector2f(80.0f, 250.0f));
    _sceneObjects[2] = new vl::Entity(VL_ASSET_IMG_NET, sf::Vector2f(450.0f, 415.0f));

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

  void Volley::newGame()
  {
    this->scoreUpdated = true;
    this->gameEnded = false;
    this->pause = false;
    this->latest_score = -1;
    // Initialize score value
    delete _score;
    _score = new Score();

    _scores[0] = 0u;
    _scores[1] = 0u;
    _lastPlayer = 0u;
    reset();
    // Randomly decide which player gets the ball
    std::random_device rd;                     // Seed
    std::mt19937 gen(rd());                    // Random number generator
    std::uniform_int_distribution<> dis(0, 1); // Random distribution: 0 or 1

    int serve_side = dis(gen); // Generate 0 or 1

    if (serve_side == 0)
    {
      std::cout << "left" << std::endl;
      // Position the ball for Player 1 (left side)
      _ball->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 + 150, 175)); // Left player's position
    }
    else
    {
      std::cout << "right" << std::endl;

      // Position the ball for Player 2 (right side)
      _ball->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 - 150, 175)); // Right player's position
    }
    _ball->handleEvent(vl::Event::LEFT);
  }

  void Volley::changeGameMode()
  {
    if (!isTwoVsTwo)
    {
      this->isTwoVsTwo = true;
      _players[2] = new vl::Character(VL_ASSET_IMG_PLAYER3, sf::Vector2f(VL_WINDOW_WIDTH / 4 - 100, 660), VL_PLAYER_FRICTION);
      _players[3] = new vl::Character(VL_ASSET_IMG_PLAYER4, sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 100, 600), VL_PLAYER_FRICTION);

      _players[2]->setPlayableArea(sf::FloatRect(VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
      _players[3]->setPlayableArea(sf::FloatRect(VL_WINDOW_WIDTH / 2 + 4 * VL_MARGIN, VL_MARGIN, VL_WINDOW_WIDTH / 2 - 4 * VL_MARGIN, VL_WINDOW_HEIGHT - VL_MARGIN));
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

  void Volley::render()
  {

    _window->clear();

    if (pause)
    {
      pauseMenu.draw(*_window);
    }
    else
    {

      // Draw background and other scene objects
      _window->draw(_sceneObjects[0]->getSprite());

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
        for (auto &player : _players)
          if (player)
            _window->draw(player->getSprite());
      }
      else
      {
        _window->draw(_players[0]->getSprite());
        _window->draw(_players[1]->getSprite());
      }

      // Render ball and other objects
      _window->draw(_ball->getSprite());
      _window->draw(_sceneObjects[2]->getSprite());
      _window->draw(_sceneObjects[1]->getSprite());
      _window->draw(_score->getSprite());

      _window->draw(player1Message);
      _window->draw(player2Message);

      // Display "Hit 1 to start" only if pause is due to a score update
      if (scoreUpdated && !didSomeoneWin())
      {
        _window->draw(startMessage);
      }
    }
    if (didSomeoneWin())
    {
      winMessage.setString(getWinner() == 0 ? "Team/Player 1 Wins!" : "Team/Player 2 Wins!");
      _window->draw(winMessage);
      _window->draw(returnMessage);
    }

    _window->display();
  }

  void Volley::resolveCollisions()
  {
    static bool player1Collision = false;
    static bool player2Collision = false;
    static bool player3Collision = false;
    static bool player4Collision = false;
    bool flag = false;

    auto angle = (_ball->getVelocity().x * 180.0f) / (32.0f * 3.14f);
    _ball->rotate(angle);

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

    if (isTwoVsTwo)
    {
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

    // Check if the ball is bounced three times without crossing the net
    if ((_ball->bounce_p1 + _ball->bounce_p3) > 3 || (_ball->bounce_p2 + _ball->bounce_p4) > 3 || flag == true)
    {
      _scores[1 - _lastPlayer]++;
      latest_score = 1 - _lastPlayer;
      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();
      reset();
      TeamPlayerServe = !TeamPlayerServe;

      return;
    }

    const sf::Vector2f p = _sceneObjects[2]->getPosition();
    const sf::Vector2u s = _sceneObjects[2]->getSize();

    sf::FloatRect net_box(sf::Vector2f(p.x + s.x / 2.0f - 10.0f, p.y + 50.0f), sf::Vector2f(20.0f, 300.0f));

    if (net_box.contains(_ball->getPosition()))
    {
      _scores[1 - _lastPlayer]++;
      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();
      reset();
      TeamPlayerServe = !TeamPlayerServe;
    }
  }

  void Volley::reset()
  {
    if (latest_score == 0) // Player 1 (left side) is leading
    {
      _ball->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 + 150, 175)); // Position on Player 1's side

      if (TeamPlayerServe)
      { //1
      }
      else // 0
      {
      }
    }
    else if (latest_score == 1) // Player 2 (right side) is leading
    {
      _ball->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 - 150, 175)); // Position on Player 2's side

      if (TeamPlayerServe) // 1
      {
      }
      else // 0
      {
      }
    }

    _ball->bounce_p1 = 0; // Reset bounce counts
    _ball->bounce_p2 = 0;
    _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 660));
    _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 600));

    if (isTwoVsTwo)
    {
      _players[2]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4 - 100, 660));
      _players[3]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 + 100, 600));
      _players[2]->stop();
      _players[3]->stop();
    }

    _ball->stop();
    _players[0]->stop();
    _players[1]->stop();

    this->scoreUpdated = true;
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

  void Volley::update()
  {
    auto dt = gameClock.restart().asSeconds();
    resolveCollisions();

    if (!pause)
    {

      if (!isBallStatic)
        _ball->update(dt);
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
      _shadows[0]->setPosition(_players[0]->getPosition().x, 690);
      _shadows[0]->setScale(_players[0]->getPosition().y / 700, 0.3 * _players[0]->getPosition().y / 700);
      _shadows[1]->setPosition(_players[1]->getPosition().x, 690);
      _shadows[1]->setScale(_players[1]->getPosition().y / 700, 0.3 * _players[1]->getPosition().y / 700);

      _shadows[2]->setPosition(_ball->getPosition().x, 690);
      _shadows[2]->setScale(_ball->getPosition().y / 700, 0.3 * _ball->getPosition().y / 700);
    }
  }

  void Volley::handleEvents()
  {
    // Start the game loop
    // Process events
    sf::Event event;
    while (_window->pollEvent(event))
    {
      if (event.type == sf::Event::KeyPressed)
      {
        if (pause || scoreUpdated)
        {
          switch (event.key.code)
          {
          case sf::Keyboard::Num1:
            if (scoreUpdated && !didSomeoneWin())
            {
              std::cout << "hit enter not pause" << std::endl;
              scoreUpdated = false;
            }
            break;
          case sf::Keyboard::P:

            std::cout << "pause while hit enter" << scoreUpdated << std::endl;
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
            case 2:
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
          switch (event.key.code)
          {
          case sf::Keyboard::P:
            handlePauseEvent();
            break;
          case sf::Keyboard::W:
            _players[0]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::I:
            _players[1]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::G:
            isBallStatic = false;
            break;
          case sf::Keyboard::V:
            if (isBallStatic)
              _ball->handleEvent(vl::Event::LEFT);
            break;
          case sf::Keyboard::B:
            if (isBallStatic)
              _ball->handleEvent(vl::Event::RIGHT);
            break;
          case sf::Keyboard::T:
            if (isTwoVsTwo)
              _players[2]->handleEvent(vl::Event::JUMP);
            break;
          case sf::Keyboard::Up:
            if (isTwoVsTwo)
              _players[3]->handleEvent(vl::Event::JUMP);
            break;

          case sf::Keyboard::Escape:
            _window->close();
            break;
            // case sf::Keyboard::Space:
            //   _ball->setPosition(sf::Vector2f(5 * VL_WINDOW_WIDTH / 8, 0));
            //   _players[0]->setPosition(sf::Vector2f(VL_WINDOW_WIDTH / 4, 0));
            //   _players[1]->setPosition(sf::Vector2f(3 * VL_WINDOW_WIDTH / 4, 0));
            //   break;

          default:
            break;
          }
        }
      }

      if (event.type == sf::Event::Closed)
        _window->close();
    }

    if (!pause && !scoreUpdated)
    {

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::A))
        _players[0]->handleEvent(vl::Event::LEFT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::D))
        _players[0]->handleEvent(vl::Event::RIGHT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::J))
        _players[1]->handleEvent(vl::Event::LEFT);

      if (sf::Keyboard::isKeyPressed(sf::Keyboard::L))
        _players[1]->handleEvent(vl::Event::RIGHT);

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
  }

  void Volley::onNotify(const vl::Event &event)
  {
    if (event == vl::Event::BALL_FELL)
    {
      if (_ball->getPosition().x < VL_WINDOW_WIDTH / 2)
      {
        _scores[1]++;
        latest_score = 1;
        TeamPlayerServe = !TeamPlayerServe;
      }
      else
      {
        _scores[0]++;
        latest_score = 0;
        TeamPlayerServe = !TeamPlayerServe;
      }

      _score->update(_scores[0], _scores[1]);
      _sounds[0]->play();

      reset();
    }
  }

  void Volley::run()
  {

    // Game loop
    while (true)
    {
      handleEvents();
      update();
      render();
      // Exit if the window is closed
      if (!_window->isOpen())
      {
        gameEnded = true;
        break;
      }
    }

    if (_window->isOpen())
    {
      _window->close();
    }
  }

  void Volley::handlePauseEvent()
  {
    pause = !pause;
  }

  bool Volley::didSomeoneWin()
  {
    return gameEnded;
  }

  int Volley::getWinner()
  {
    return (_scores[0] >= WINNING_SCORE) ? 0 : 1;
  }
}
