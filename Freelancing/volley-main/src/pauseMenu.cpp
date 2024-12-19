// PauseMenu.cpp
#include "pauseMenu.hpp"

namespace vl {

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

void PauseMenu::draw(sf::RenderWindow& window) {
    for (const auto& option : PauseMenuOptions) {
        window.draw(option);
    }
}

void PauseMenu::moveUp() {
    if (selectedOptionIndex > 0) {
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex--;
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

void PauseMenu::moveDown() {
    if (selectedOptionIndex < PauseMenuOptions.size() - 1) {
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex++;
        PauseMenuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

int PauseMenu::getSelectedOption() const {
    return selectedOptionIndex;
}

} // namespace vl
