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

#include <iostream>
#include "score.hpp"
#include "constants.hpp"
#include <sstream>

namespace vl {

  /**
   * @brief Constructor for the Score class.
   * 
   * Initializes the score display with a default font and text ("0 - 0").
   * If the font fails to load, an error message is displayed.
   * The score text is positioned in the center of the window horizontally
   * and set at a fixed vertical position.
   */
  Score::Score(): _font(), _text() {
    if (!_font.loadFromFile("arial.ttf")) {
      std::cerr << "Fail to load font :(\n";
    }

    _text.setFont(_font);
    _text.setString("0 - 0");
    _text.setCharacterSize(36);
    _text.setFillColor(sf::Color::White);

    auto size = _text.getLocalBounds().width;
    _text.setPosition(sf::Vector2f((VL_WINDOW_WIDTH - size) / 2, 100));
    //_ text.setStyle(sf::Text::Bold | sf::Text::Underlined);
  }

  /**
   * @brief Get the sprite representing the score.
   * 
   * Returns the sf::Drawable object that is used for rendering the score.
   * In this case, it returns the _text object representing the score as text.
   * 
   * @return A reference to the sf::Drawable object representing the score.
   */
  const sf::Drawable& Score::getSprite() const {
    return _text;
  }

  /**
   * @brief Update the score display.
   * 
   * Updates the score text to reflect the new scores provided for both players.
   * The score is updated in the format "score1 - score2", and the text is
   * repositioned in the center horizontally.
   * 
   * @param s1 The score for player 1.
   * @param s2 The score for player 2.
   */
  void Score::update(unsigned int s1, unsigned int s2) {
    std::ostringstream stream;
    stream << s1 << " - " << s2;
    _text.setString(stream.str().c_str());
    auto size = _text.getLocalBounds().width;
    _text.setPosition(sf::Vector2f((VL_WINDOW_WIDTH - size) / 2, 100));
  }
};

