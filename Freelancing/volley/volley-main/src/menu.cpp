// Menu.cpp
#include "Menu.hpp"

namespace vl
{
    /**
     * @brief Construct a new Menu object.
     *
     * Initializes the menu with the specified window dimensions and loads the
     * font required for rendering the menu options. It also sets up the default
     * menu options and positions them on the screen.
     *
     * @param width The width of the window.
     * @param height The height of the window.
     *
     * @throws std::runtime_error if the font cannot be loaded.
     */

    Menu::Menu(float width, float height) : selectedOptionIndex(0)
    {
        if (!font.loadFromFile("arial.ttf"))
        {
            throw std::runtime_error("Failed to load font");
        }

        std::vector<std::string> options = {
            "Start Game",
            "Select Game Mode",
            "View Instructions",
            "Exit"};

        // Set up menu options

        for (size_t i = 0; i < options.size(); ++i)
        {
            sf::Text text;
            text.setFont(font);
            text.setString(options[i]);
            text.setFillColor(i == selectedOptionIndex ? sf::Color::Red : sf::Color::White);
            text.setPosition(sf::Vector2f(width / 2.5f, height / (options.size() + 1) * (i + 1)));
            menuOptions.push_back(text);
        }
    }

    /**
     * @brief Draw the menu on the provided window.
     *
     * This method renders all the menu options on the screen using the
     * specified window object.
     *
     * @param window The window on which the menu is drawn.
     */
    void Menu::draw(sf::RenderWindow &window)
    {
        for (const auto &option : menuOptions)
        {
            window.draw(option);
        }
    }

    /**
     * @brief Move the selection up in the menu.
     *
     * Changes the selected option to the one above the current selection,
     * and updates the color of the selected option to red.
     */
    void Menu::moveUp()
    {
        if (selectedOptionIndex > 0)
        {
            menuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
            selectedOptionIndex--;
            menuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
        }
    }

    /**
     * @brief Move the selection down in the menu.
     *
     * Changes the selected option to the one below the current selection,
     * and updates the color of the selected option to red.
     */
    void Menu::moveDown()
    {
        if (selectedOptionIndex < menuOptions.size() - 1)
        {
            menuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
            selectedOptionIndex++;
            menuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
        }
    }

    /**
     * @brief Get the index of the currently selected option.
     *
     * @return The index of the selected menu option.
     */
    int Menu::getSelectedOption() const
    {
        return selectedOptionIndex;
    }

} // namespace vl
