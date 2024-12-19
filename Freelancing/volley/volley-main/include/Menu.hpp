// Menu.hpp
#ifndef MENU_HPP
#define MENU_HPP

#include <SFML/Graphics.hpp>
#include <vector>
#include <string>

namespace vl {

class Menu {
public:
    Menu(float width, float height);
    ~Menu() = default;

    void draw(sf::RenderWindow& window);
    void moveUp();
    void moveDown();
    int getSelectedOption() const;

private:
    int selectedOptionIndex;
    sf::Font font;
    std::vector<sf::Text> menuOptions;
};

} // namespace vl

#endif // MENU_HPP
