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

#ifndef VL_ASSETS_HPP
#define VL_ASSETS_HPP

/**
 * @file vl_assets.hpp
 * @brief Defines paths for various asset files (images and sounds) used in the Volley game.
 * 
 * This header file includes the file paths for the images and sounds used in the game. 
 * The assets include player images, background images, sound effects for events like 
 * losing and bouncing, and other game objects like the net and tree.
 */

/**
 * @def VL_ASSETS_IMG
 * @brief The directory path for image assets.
 */
#define VL_ASSETS_IMG        "img/"

/**
 * @def VL_ASSET_IMG_BALL
 * @brief The path to the ball image asset.
 */
#define VL_ASSET_IMG_BALL    VL_ASSETS_IMG "ball.png"

/**
 * @def VL_ASSET_IMG_PLAYER1
 * @brief The path to the image of player 1.
 */
#define VL_ASSET_IMG_PLAYER1 VL_ASSETS_IMG "player.png"

/**
 * @def VL_ASSET_IMG_PLAYER2
 * @brief The path to the image of player 2.
 */
#define VL_ASSET_IMG_PLAYER2 VL_ASSETS_IMG "player2.png"

/**
 * @def VL_ASSET_IMG_PLAYER3
 * @brief The path to the image of player 3.
 */
#define VL_ASSET_IMG_PLAYER3 VL_ASSETS_IMG "player3.png"

/**
 * @def VL_ASSET_IMG_PLAYER4
 * @brief The path to the image of player 4.
 */
#define VL_ASSET_IMG_PLAYER4 VL_ASSETS_IMG "player4.png"

/**
 * @def VL_ASSET_IMG_BEACH
 * @brief The path to the image of the beach background.
 */
#define VL_ASSET_IMG_BEACH   VL_ASSETS_IMG "beach.png"

/**
 * @def VL_ASSET_IMG_NET
 * @brief The path to the image of the net object in the game.
 */
#define VL_ASSET_IMG_NET     VL_ASSETS_IMG "net.png"

/**
 * @def VL_ASSET_IMG_TREE
 * @brief The path to the image of the tree object in the game.
 */
#define VL_ASSET_IMG_TREE    VL_ASSETS_IMG "tree.png"

/**
 * @def VL_ASSETS_SND
 * @brief The directory path for sound assets.
 */
#define VL_ASSETS_SND       "sounds/"

/**
 * @def VL_ASSET_SND_LOSE
 * @brief The path to the sound effect for losing in the game.
 */
#define VL_ASSET_SND_LOSE   VL_ASSETS_SND "lose.wav"

/**
 * @def VL_ASSET_SND_BOUNCE
 * @brief The path to the sound effect for the ball bouncing.
 */
#define VL_ASSET_SND_BOUNCE VL_ASSETS_SND "bounce.wav"

#endif
