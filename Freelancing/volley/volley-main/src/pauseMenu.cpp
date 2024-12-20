
//PauseMenu.cpp
#include "pauseMenu.hpp"
namespace vl {

/**
 * @brief Construct a new PauseMenu object.
 * 
 * Initializes the pause menu with the specified window dimensions and loads 
 * the font required for rendering the menu options. It also initializes the
 * list of menu options (such as "Resume", "Start New Game", etc.) and sets 
 * the initial selected option.
 * 
 * @param width The width of the window.
 * @param height The height of the window.
 * @throws std::runtime_error If the font fails to load.
 */
PauseMenu::PauseMenu(float width, float height) : selectedOptionIndex(0) {
    if (!font.loadFromFile("arial.ttf")) {
        throw std::runtime_error("Failed to load font");
    }

    std::vector<std::string> options = {
        "Resume",
        "Start New Game",
        "Change Game Mode",
        "Exit to Main Menu"
    };

    for (size_t i = 0; i < options.size(); ++i) {
        sf::Text text;
        text.setFont(font);
        text.setString(options[i]);
        text.setFillColor(i == selectedOptionIndex ? sf::Color::Red : sf::Color::White);
        text.setPosition(sf::Vector2f(width / 2.5f, height / (options.size() + 1) * (i + 1)));
        PauseMenuOptions.push_back(text);
    }
}

/**
 * @brief Draw the pause menu on the specified window.
 * 
 * Renders all the pause menu options on the screen using the provided window 
 * object. Each option is drawn in the order they are added to the `PauseMenuOptions` list.
 * 
 * @param window The window on which the pause menu is drawn.
 */
void PauseMenu::draw(sf::RenderWindow& window) {
    for (const auto& option : PauseMenuOptions) {
        window.draw(option);
    }
}

/**
 * @brief Move the selection up in the pause menu.
 * 
 * Changes the selected option to the one above the current selection, and 
 * updates the color of the selected option to indicate the selection.
 */
void PauseMenu::moveUp() {
    if (selectedOptionIndex > 0) {
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex--;
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

/**
 * @brief Move the selection down in the pause menu.
 * 
 * Changes the selected option to the one below the current selection, and 
 * updates the color of the selected option to indicate the selection.
 */
void PauseMenu::moveDown() {
    if (selectedOptionIndex < PauseMenuOptions.size() - 1) {
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex++;
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

/**
 * @brief Get the index of the currently selected option in the pause menu.
 * 
 * @return The index of the selected pause menu option.
 */
int PauseMenu::getSelectedOption() const {
    return selectedOptionIndex;
}

} // namespace vl
