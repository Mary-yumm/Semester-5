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


#ifndef VL_SOUND_HPP
#define VL_SOUND_HPP

#include <SFML/Audio.hpp>

namespace vl {

/**
 * @brief A class to handle sound playback.
 * 
 * The Sound class provides functionality to load and play sounds using 
 * the SFML library. The constructor loads a sound file, and the `play` 
 * method plays the sound.
 */
  class Sound {
  public:
    /**
     * @brief Construct a new Sound object.
     * 
     * Initializes the sound object by loading a sound file into the sound buffer.
     * 
     * @param file The path to the sound file to be loaded.
     */
    Sound(const char* file);

    /**
     * @brief Play the sound.
     * 
     * Plays the sound that has been loaded into the sound buffer.
     */
    void play();

  private:
    sf::Sound _sound; ///< SFML sound object used to play the sound.
    sf::SoundBuffer _buffer; ///< SFML buffer used to store the sound data.
  };
};

#endif