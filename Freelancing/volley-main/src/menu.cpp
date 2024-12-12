// Menu.cpp
#include "Menu.hpp"

namespace vl {

Menu::Menu(float width, float height) : selectedOptionIndex(0) {
    if (!font.loadFromFile("arial.ttf")) {
        throw std::runtime_error("Failed to load font");
    }

    std::vector<std::string> options = {
        "Start Game",
        "Select Game Mode",
        "View Instructions",
        "Exit"
    };

    for (size_t i = 0; i < options.size(); ++i) {
        sf::Text text;
        text.setFont(font);
        text.setString(options[i]);
        text.setFillColor(i == selectedOptionIndex ? sf::Color::Red : sf::Color::White);
        text.setPosition(sf::Vector2f(width / 2.5f, height / (options.size() + 1) * (i + 1)));
        menuOptions.push_back(text);
    }
}

void Menu::draw(sf::RenderWindow& window) {
    for (const auto& option : menuOptions) {
        window.draw(option);
    }
}

void Menu::moveUp() {
    if (selectedOptionIndex > 0) {
        menuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex--;
        menuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

void Menu::moveDown() {
    if (selectedOptionIndex < menuOptions.size() - 1) {
        menuOptions[selectedOptionIndex].setFillColor(sf::Color::White);
        selectedOptionIndex++;
        menuOptions[selectedOptionIndex].setFillColor(sf::Color::Red);
    }
}

int Menu::getSelectedOption() const {
    return selectedOptionIndex;
}

} // namespace vl
