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

#include "sound.hpp"

namespace vl {

/**
 * @brief Construct a new Sound object.
 * 
 * This constructor initializes the sound object by loading the sound file
 * specified by the given file path and setting it to the sound buffer.
 * 
 * @param file The path to the sound file to be loaded into the sound buffer.
 */
  Sound::Sound(const char* file): _sound(), _buffer() {
    if (!_buffer.loadFromFile(file)) {
      throw std::runtime_error("Failed to load sound file");
    }
    _sound.setBuffer(_buffer);
  }

  /**
   * @brief Play the sound.
   * 
   * Plays the sound if it is not already playing. The method checks the
   * current status of the sound and only plays it if it is not already
   * playing to avoid multiple overlapping plays.
   */
  void Sound::play() {
    if (_sound.getStatus() != sf::Sound::Status::Playing)
      _sound.play();
  }
};

