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
#include <memory>

enum class AppState
{
    MENU,
    GAME,
    INSTRUCTIONS,
    GAME_MODE_SELECTION
};

void showInstructions(sf::RenderWindow &window);
void selectGameMode(sf::RenderWindow &window, bool &isTwoVsTwo);
void showWinningScreen(unsigned int winner, sf::RenderWindow &window, sf::Font &font);

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
        std::cout << "hyyyy" << std::endl;
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
            // window.setVisible(false);
            window.close();
            vl::Volley *application = new vl::Volley(isTwoVsTwo);
            // application->run();
            //std::unique_ptr<vl::Volley> application = std::make_unique<vl::Volley>(isTwoVsTwo);
            application->run();

            std::cout << "mainnnn" << std::endl;
            // Check if the game ended due to a winning condition
            // if (application->didSomeoneWin())
            // {
            //     window.setVisible(true); // Show the main menu again
            //     window.clear();
            //     currentState = AppState::MENU;
            //     std::cout << "Displaying winning screen" << application->getWinner() << std::endl;
            //     // sf::RenderWindow winScreen(sf::VideoMode(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT), "Game Over");
            //     //showWinningScreen(application->getWinner(), window, font);
            //     // winScreen.close();
            //     // std::cout << "Screen closed successfully" << std::endl;
            // }

            //delete application;
            std::cout << "width and height: " << VL_WINDOW_WIDTH << " " << VL_WINDOW_HEIGHT << std::endl;
            window.create(sf::VideoMode(VL_WINDOW_WIDTH, VL_WINDOW_HEIGHT), "Volleyball Game Menu");
            currentState = AppState::MENU;
            //application.reset(); // Explicitly clean up the game application
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
        std::cout << "heyyyyy" << std::endl;
    }

    return 0;
}

void showWinningScreen(unsigned int winner, sf::RenderWindow &window, sf::Font &font)
{
    std::cout << "heyyiii" << std::endl;

    sf::Text winMessage;
    winMessage.setFont(font);
    winMessage.setString(winner == 0 ? "Team/Player 1 Wins!" : "Team/Player 2 Wins!");
    winMessage.setCharacterSize(50);
    winMessage.setFillColor(sf::Color::White);
    winMessage.setStyle(sf::Text::Bold);

    sf::FloatRect winBounds = winMessage.getGlobalBounds();
    winMessage.setPosition(
        (window.getSize().x - winBounds.width) / 2,
        (window.getSize().y - winBounds.height) / 3);

    sf::Text returnMessage;
    returnMessage.setFont(font);
    returnMessage.setString("Press Enter to return to Main Menu");
    returnMessage.setCharacterSize(30);
    returnMessage.setFillColor(sf::Color::White);

    sf::FloatRect returnBounds = returnMessage.getGlobalBounds();
    returnMessage.setPosition(
        (window.getSize().x - returnBounds.width) / 2,
        (window.getSize().y - returnBounds.height) / 1.5);
    std::cout << "heyyiii2" << std::endl;
    window.clear();
    window.draw(winMessage);
    window.draw(returnMessage);
    window.display();
    // while (true)
    // {
    //     sf::Event event;

    //     while (window.pollEvent(event))
    //     {
    //         if (event.type == sf::Event::KeyPressed && event.key.code == sf::Keyboard::Enter)
    //         {
    //             return;
    //         }
    //         if (event.type == sf::Event::Closed)
    //         {
    //             std::cout << "close" << std::endl;

    //             window.close();
    //             return;
    //         }
    //     }
    // }
    // Wait for a key press to return to the menu
    sf::Event event;
    while (window.waitEvent(event))
    {
        if (event.type == sf::Event::KeyPressed)
            break;
    }

    std::cout << "end" << std::endl;
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
