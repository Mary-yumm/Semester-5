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

/**
 * @file main.cpp
 * @brief Entry point and main logic for the Volleyball Game Menu application.
 */

#include <SFML/Graphics.hpp>
#include <iostream>
#include "Menu.hpp"
#include "volley.hpp"
#include <memory>

/**
 * @enum AppState
 * @brief Represents the current state of the application.
 */
enum class AppState
{
    MENU,               ///< Main menu state.
    GAME,               ///< Game state.
    INSTRUCTIONS,       ///< Instructions screen state.
    GAME_MODE_SELECTION ///< Game mode selection screen state.
};

/**
 * @brief Displays the instructions screen.
 * @param window The SFML window where the instructions are rendered.
 */
void showInstructions(sf::RenderWindow &window);

/**
 * @brief Displays the game mode selection screen.
 * @param window The SFML window where the game mode selection is rendered.
 * @param isTwoVsTwo A reference to a boolean that tracks whether the game mode is 2v2.
 */
void selectGameMode(sf::RenderWindow &window, bool &isTwoVsTwo);

/**
 * @brief Displays the winning screen.
 * @param winner The ID of the winning player.
 * @param window The SFML window where the winning screen is rendered.
 * @param font The font used for rendering text.
 */
void showWinningScreen(unsigned int winner, sf::RenderWindow &window, sf::Font &font);

/**
 * @brief Main function for the Volleyball Game Menu application.
 * @param argc Argument count.
 * @param argv Argument vector.
 * @return Exit status.
 */

int main(int argc, char **argv)
{
    (void)argc;
    (void)argv;

    sf::RenderWindow window(sf::VideoMode(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT), "Volleyball Game Menu");

    vl::Menu menu(window.getSize().x, window.getSize().y);

    AppState currentState = AppState::MENU;
    bool isTwoVsTwo = false; // Tracks if the game mode is 2v2
    sf::Font font;
    if (!font.loadFromFile("arial.ttf"))
    {
        std::cerr << "Failed to load font\n";
        return -1;
    }
    unsigned int winner = 0; // Store the winner for the winning screen

    while (window.isOpen())
    {
        sf::Event event;

        // Handle events
        while (window.pollEvent(event))
        {
            if (event.type == sf::Event::Closed)
                window.close();

            if (currentState == AppState::MENU)
            {
                if (event.type == sf::Event::KeyPressed)
                {
                    if (event.key.code == sf::Keyboard::Up)
                    {
                        menu.moveUp();
                    }
                    else if (event.key.code == sf::Keyboard::Down)
                    {
                        menu.moveDown();
                    }
                    else if (event.key.code == sf::Keyboard::Enter)
                    {
                        switch (menu.getSelectedOption())
                        {
                        case 0: // Start game
                            currentState = AppState::GAME;
                            break;
                        case 1: // Select game mode
                            currentState = AppState::GAME_MODE_SELECTION;
                            break;
                        case 2: // View instructions
                            currentState = AppState::INSTRUCTIONS;
                            break;
                        case 3: // Exit
                            window.close();
                            break;
                        }
                    }
                }
            }
        }

        window.clear();
        // Render based on the current state
        if (currentState == AppState::MENU)
        {
            menu.draw(window);
            window.display();
        }
        else if (currentState == AppState::GAME)
        {
            window.close();
            vl::Volley *application = new vl::Volley(isTwoVsTwo);
            application->run();
            isTwoVsTwo = false; // Tracks if the game mode is 2v2

            window.create(sf::VideoMode(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT), "Volleyball Game Menu");
            currentState = AppState::MENU;
        }
        else if (currentState == AppState::INSTRUCTIONS)
        {
            showInstructions(window);

            // Return to menu after displaying instructions
            currentState = AppState::MENU;
        }
        else if (currentState == AppState::GAME_MODE_SELECTION)
        {
            selectGameMode(window, isTwoVsTwo);

            // Return to menu after selecting game mode
            currentState = AppState::MENU;
        }
    }

    return 0;
}

// Function to display the instructions screen
void showInstructions(sf::RenderWindow &window)
{
    sf::Font font;
    if (!font.loadFromFile("arial.ttf"))
    {
        throw std::runtime_error("Failed to load font");
    }

    sf::Text instructions;
    instructions.setFont(font);
    instructions.setString(
        "Instructions:\n"
        "- Use arrow keys to navigate the menu.\n"
        "- Press Enter to go back to the main menu.\n"
        "- Player 1 controls : W,A,D.\n"
        "- Player 2 controls : I,J,L\n"
        "- Player 3 controls : F,T,H\n"
        "- Player 4 controls : Arrow Keys\n"
        "- Press P to pause the game\n"
        "- Press b/v to adjsut ball position during serving. G to drop\n"
        "- Have fun playing the volleyball game!");
    instructions.setCharacterSize(20);
    instructions.setFillColor(sf::Color::White);

    // Get the size of the text and the window
    sf::FloatRect textBounds = instructions.getLocalBounds();
    sf::Vector2u windowSize = window.getSize();

    // Calculate centered position
    float x = (windowSize.x - textBounds.width) / 2.0f - textBounds.left;
    float y = (windowSize.y - textBounds.height) / 2.0f - textBounds.top;
    instructions.setPosition(x, y);

    // Draw the instructions
    window.clear(); // Clear the window to avoid overlapping
    window.draw(instructions);
    window.display();

    // Wait for a key press to return to the menu
    sf::Event event;
    while (window.waitEvent(event))
    {
        if (event.type == sf::Event::KeyPressed)
            break;
    }
}

// Function to display the game mode selection screen
void selectGameMode(sf::RenderWindow &window, bool &isTwoVsTwo)
{
    sf::Font font;
    if (!font.loadFromFile("arial.ttf"))
    {
        throw std::runtime_error("Failed to load font");
    }

    sf::Text title;
    title.setFont(font);
    title.setString("Select Game Mode:");
    title.setCharacterSize(30);
    title.setFillColor(sf::Color::White);
    title.setPosition(50, 50);

    sf::Text mode1v1;
    mode1v1.setFont(font);
    mode1v1.setString("1v1");
    mode1v1.setCharacterSize(24);
    mode1v1.setFillColor(sf::Color::Red);
    mode1v1.setPosition(100, 150);

    sf::Text mode2v2;
    mode2v2.setFont(font);
    mode2v2.setString("2v2");
    mode2v2.setCharacterSize(24);
    mode2v2.setFillColor(sf::Color::White);
    mode2v2.setPosition(100, 200);

    int selectedModeIndex = 0;

    while (true)
    {
        window.clear();
        window.draw(title);
        window.draw(mode1v1);
        window.draw(mode2v2);
        window.display();

        sf::Event event;
        while (window.pollEvent(event))
        {
            if (event.type == sf::Event::KeyPressed)
            {
                if (event.key.code == sf::Keyboard::Up || event.key.code == sf::Keyboard::Down)
                {
                    selectedModeIndex = 1 - selectedModeIndex;
                    mode1v1.setFillColor(selectedModeIndex == 0 ? sf::Color::Red : sf::Color::White);
                    mode2v2.setFillColor(selectedModeIndex == 1 ? sf::Color::Red : sf::Color::White);
                }
                else if (event.key.code == sf::Keyboard::Enter)
                {
                    isTwoVsTwo = (selectedModeIndex == 1);
                    return;
                }
            }
        }
    }
}
