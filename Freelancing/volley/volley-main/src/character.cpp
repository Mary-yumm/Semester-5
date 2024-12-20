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

#include "character.hpp"
#include "constants.hpp"

namespace vl
{

  /**
   * @brief Constructor for the Character class.
   *
   * Initializes the character with a texture loaded from the specified file, sets its position,
   * and defines the friction factor. The sprite’s origin is set to the center of the texture.
   *
   * @param file The path to the texture file to be used for the character.
   * @param position The initial position of the character.
   * @param friction The friction factor applied to the character’s movement.
   */
  Character::Character(const char *file, const sf::Vector2f &position, float friction)
      : Entity(file, position)
  {
    _sprite.setOrigin(sf::Vector2f(_texture.getSize() / 2u)); ///< Set sprite origin to center of texture
    _friction = friction;                                     ///< Set friction for character's movement
  }

  /**
   * @brief Handles input events to update the character’s state and perform actions.
   *
   * Depending on the event, the character’s state is updated and appropriate actions are taken.
   * This includes resetting the character, jumping, and moving left or right.
   *
   * @param e The event to handle (e.g., RESET, JUMP, RIGHT, LEFT).
   */
  void Character::handleEvent(vl::Event e)
  {
    switch (e)
    {
    case Event::RESET:
      _state = vl::State::IDLE; ///< Set character state to IDLE
      reset();                  ///< Reset character attributes (e.g., position)
      break;

    case Event::JUMP:
      if (_state != vl::State::JUMPING)
      {                              ///< Ensure the character is not already jumping
        _state = vl::State::JUMPING; ///< Set state to JUMPING
        jump(VL_JUMP_STEP);          ///< Execute jump with the specified jump step
      }
      break;

    case Event::RIGHT:
      _state = vl::State::GOING_RIGHT;        ///< Set state to GOING_RIGHT
      move(sf::Vector2f(VL_MOVE_STEP, 0.0f)); ///< Move the character to the right
      break;

    case Event::LEFT:
      _state = vl::State::GOING_LEFT;          ///< Set state to GOING_LEFT
      move(sf::Vector2f(-VL_MOVE_STEP, 0.0f)); ///< Move the character to the left
      break;

    default:
      break; ///< No action for other events
    }
  }

}
