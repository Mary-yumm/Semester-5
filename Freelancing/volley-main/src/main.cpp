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

// #include "volley.hpp"

// int main(int argc, char **argv)
// {
//   (void)argc;
//   (void)argv;

//   vl::Volley *application = new vl::Volley();

//   application->run();

//   delete application;

//   return 0;
// }
#include <SFML/Graphics.hpp>
#include <iostream>
#include "Menu.hpp"
#include "volley.hpp"

enum class AppState
{
    MENU,
    GAME,
    INSTRUCTIONS,
    GAME_MODE_SELECTION
};

void showInstructions(sf::RenderWindow &window);
void selectGameMode(sf::RenderWindow &window, bool &isTwoVsTwo);

int main(int argc, char **argv)
{
    (void)argc;
    (void)argv;

    sf::RenderWindow window(sf::VideoMode(800, 600), "Volleyball Game Menu");

    vl::Menu menu(window.getSize().x, window.getSize().y);

    AppState currentState = AppState::MENU;
    bool isTwoVsTwo = false; // Tracks if the game mode is 2v2

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

        // Render based on the current state
        if (currentState == AppState::MENU)
        {
            window.clear();
            menu.draw(window);
            window.display();
        }
        else if (currentState == AppState::GAME)
        {
            window.close(); // Close the menu window
            vl::Volley *application = new vl::Volley(isTwoVsTwo);
            application->run();
            std::cout << "mainnnn" << std::endl;
            // Check if the game ended due to a winning condition
            if (application->didSomeoneWin())
            {
                std::cout << "Displaying winning screen" << std::endl;
                application->showWinningScreen(application->getWinner());
            }
            delete application;
            currentState = AppState::MENU;
            window.create(sf::VideoMode(800, 600), "Volleyball Game Menu");
        }
        else if (currentState == AppState::INSTRUCTIONS)
        {
            window.clear();
            showInstructions(window);

            // Return to menu after displaying instructions
            currentState = AppState::MENU;
        }
        else if (currentState == AppState::GAME_MODE_SELECTION)
        {
            window.clear();
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
        "- Press Enter to select an option.\n"
        "- In the game, use the controls as described.\n"
        "- Have fun playing the volleyball game!");
    instructions.setCharacterSize(20);
    instructions.setFillColor(sf::Color::White);
    instructions.setPosition(50, 50);

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
