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

#ifndef VL_BALL_HPP
#define VL_BALL_HPP

#include "interfaces.hpp"
#include "entity.hpp"
#include "observer.hpp"

namespace vl
{

  /**
   * @brief Represents a ball in the game, implementing several interfaces to handle
   * physical behavior, collisions, and events.
   *
   * The Ball class inherits from `Entity` and implements `ISolidObject`, `ISoftObject`,
   * and `IControllableObject` to provide functionality for collision detection, bouncing,
   * and event handling.
   */
  class Ball : public Entity, public ISolidObject, public ISoftObject, public IControllableObject
  {
  public:
    /**
     * @brief Constructor for the Ball class.
     *
     * Initializes the ball's texture, position, and sets the friction value.
     *
     * @param file Path to the image file for the ball texture.
     * @param position Initial position of the ball in the game world.
     * @param friction Friction value that affects the ball's movement.
     */
    Ball(const char *file, const sf::Vector2f &position, float friction, unsigned int &lastPlayer);

    /**
     * @brief Adds an observer to the ball.
     *
     * This allows the ball to notify the observer of any events that occur.
     *
     * @param observer The observer object that will be notified of events.
     */
    void setObserver(IObserver *observer);

    /**
     * @brief Notifies an event to the observer.
     *
     * Sends an event to the observer so that it can take appropriate action.
     *
     * @param event The event that needs to be notified.
     */
    void notify(Event event);

    /**
     * @brief Checks if the ball is colliding with another physical object.
     *
     * This method is an implementation of the `ISolidObject` interface and checks if
     * the ball is colliding with another object in the game world.
     *
     * @param object The other physical object to check for collision.
     * @return `true` if the two objects are colliding, otherwise `false`.
     */
    bool isCollidingWith(const IPhysicalObject &object) const;

    /**
     * @brief Bounces the ball off another physical object.
     *
     * This method is an implementation of the `ISoftObject` interface and defines how
     * the ball reacts when it bounces off another object.
     *
     * @param object The object with which the ball will bounce.
     */
    void bounce(const IPhysicalObject &object);

    void bounce_serving();

    /**
     * @brief Handles an event affecting the ball.
     *
     * Processes the given event (such as movement or other actions) and updates the ball's state.
     *
     * @param e The event to be handled.
     */
    void handleEvent(vl::Event e);

    /**
     * @brief Updates the ball's state based on the time passed since the last update.
     *
     * This method updates the ball's position, velocity, and other properties based on the
     * elapsed time, typically called once per frame.
     *
     * @param dt The time elapsed since the last update (delta time).
     */
    void update(float dt);

    void rotateLeft();
    void rotateRight();
    void updateBallPosition();

  private:
    IObserver *_observer; ///< Pointer to the observer that will be notified of events.
    float preVelX;        ///< The ball's previous velocity along the X-axis.
    float preVelY;        ///< The ball's previous velocity along the Y-axis.

  public:
    int bounce_p1;            ///< Bounce counter for the first condition.
    int bounce_p2;            ///< Bounce counter for the second condition.
    int bounce_p3;            ///< Bounce counter for the third condition.
    int bounce_p4;            ///< Bounce counter for the fourth condition.
    sf::Texture arrowTexture; ///< Texture for the arrow
    sf::Sprite arrowSprite;   ///< Sprite for the arrow
    unsigned int side;
  };
};

#endif
