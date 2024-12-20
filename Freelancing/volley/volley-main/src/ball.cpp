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

#include <SFML/Graphics.hpp>
#include "ball.hpp"
#include "utils.hpp"
#include "constants.hpp"
#include <iostream>
#include <random>

namespace vl
{
  /**
   * @brief Constructor to initialize the Ball object.
   *
   * This constructor sets the initial position of the ball and assigns the friction value.
   * It also randomly selects which player gets the ball.
   * @param file The file path for the ball texture.
   * @param position The initial position of the ball.
   * @param friction The friction value to apply to the ball's movement.
   */
  Ball::Ball(const char *file, const sf::Vector2f &position, float friction) : Entity(file, position)
  {
    _sprite.setOrigin(sf::Vector2f(_texture.getSize() / 2u));
    _friction = friction;
    bounce_p1 = 0;
    bounce_p2 = 0;
    bounce_p3 = 0;
    bounce_p4 = 0;

    // Randomly decide which player gets the ball
    std::random_device rd;                     // Seed
    std::mt19937 gen(rd());                    // Random number generator
    std::uniform_int_distribution<> dis(0, 1); // Random distribution: 0 or 1

    int serve_side = dis(gen); // Generate 0 or 1

    if (serve_side == 0)
    {
      _position = sf::Vector2f(VL_WINDOW_WIDTH / 4 + 150, 175); // Set internal position for Player 1 (left side)
    }
    else
    {
      _position = sf::Vector2f(3 * VL_WINDOW_WIDTH / 4 - 150, 175); // Set internal position for Player 2 (right side)
    }

    // Set the sprite's position based on the updated internal position
    _sprite.setPosition(_position);
  }

  /**
   * @brief Check if the ball is colliding with a physical object.
   *
   * This method checks if the ball is colliding with another physical object
   * by calculating the distance between them and comparing it with the collision threshold.
   * @param object The physical object to check for collision.
   * @return True if the ball is colliding with the object, otherwise false.
   */
  bool Ball::isCollidingWith(const IPhysicalObject &object) const
  {
    return vl::utils::sd(object.getPosition() - sf::Vector2f(0.0f, 65.0f), _position) < VL_DIST_BEFORE_COLLISION;
  }

  /**
   * @brief Handle the bounce effect when the ball collides with a physical object.
   *
   * This method calculates the bounce vector and applies it to the ball's acceleration
   * based on the object's position and the ball's current position.
   * @param object The physical object the ball is bouncing off of.
   */
  void Ball::bounce(const IPhysicalObject &object)
  {
    const sf::Vector2f &v = vl::utils::nv(_position, (object.getPosition() - sf::Vector2f(0.0f, 65.0f)));

    // Use larger bounds for testing
    const float bounceFactor = 3.0f;              // Increase further if necessary
    _acceleration.x = bounceFactor * 10.0f * v.x; // Remove clamping temporarily
    _acceleration.y = bounceFactor * 10.0f * v.y; // Remove clamping temporarily
  }

  /**
   * @brief Set the observer for the ball.
   *
   * This method sets the observer object that will be notified of events related to the ball.
   * @param observer The observer object.
   */
  void Ball::setObserver(IObserver *observer)
  {
    _observer = observer;
  }

  /**
   * @brief Notify the observer of an event.
   *
   * This method sends an event to the observer for handling. The observer will
   * take the necessary actions based on the event.
   * @param event The event to notify the observer about.
   */
  void Ball::notify(Event event)
  {
    _observer->onNotify(event);
  }

  /**
   * @brief Handle an event related to the ball.
   *
   * This method handles various events such as resetting the ball, making it jump,
   * or moving it left or right. It updates the ball's state and position accordingly.
   * @param e The event to handle.
   */
  void Ball::handleEvent(vl::Event e)
  {
    switch (e)
    {
    case Event::RESET:
      _state = vl::State::IDLE;
      reset();
      break;

    // case Event::JUMP:
    //   if (_state != vl::State::JUMPING)
    //   {
    //     _state = vl::State::JUMPING;
    //     jump(VL_JUMP_STEP);
    //   }
    //   break;

    case Event::RIGHT:
      if (_area.contains(_position + sf::Vector2f(VL_MOVE_STEP, 0.0f)))
      {
        _position.x += VL_MOVE_STEP; // Move directly to the right
        setPosition(_position);      // Update the sprite's position
      }
      break;

    case Event::LEFT:

      if (_area.contains(_position + sf::Vector2f(-VL_MOVE_STEP, 0.0f)))
      {

        _position.x -= VL_MOVE_STEP; // Move directly to the left
        setPosition(_position);      // Update the sprite's position
      }

      break;

    default:
      break;
    }
    _sprite.setPosition(_position);
  }

  /**
   * @brief Update the ball's position based on velocity and apply friction.
   *
   * This method updates the ball's position by applying velocity and delta time.
   * It also handles boundary collisions and notifies the observer if the ball falls.
   * @param dt The delta time since the last update.
   */
  void Ball::update(float dt)
  {

    // Update position based on velocity and delta time
    _position.x += _velocity.x * dt;
    _position.y += _velocity.y * dt;

    Entity::update(dt); // Use the actual delta time

    if (_position.x < VL_MARGIN)
    {
      _position.x = VL_MARGIN;
      _velocity.x *= -VL_BOUND_RESTITUTION / 2.0f;
    }
    else if (_position.x > (VL_WINDOW_WIDTH - VL_MARGIN))
    {
      _position.x = (VL_WINDOW_WIDTH - VL_MARGIN);
      _velocity.x *= -VL_BOUND_RESTITUTION / 2.0f;
    }

    if (_position.y < 0)
    {
      _velocity.y *= -VL_BOUND_RESTITUTION / 2.0f;
      _position.y = 1.0f;
    }

    auto size = getSize();
    if (_position.y > VL_FLOOR - (size.y / 2.0f) - 5.0f)
    {
      notify(vl::Event::BALL_FELL);
    }
  }

}
