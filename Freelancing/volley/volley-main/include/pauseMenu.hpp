// Menu.hpp
#ifndef PAUSEMENU_HPP
#define PAUSEMENU_HPP

#include <SFML/Graphics.hpp>
#include <vector>
#include <string>

namespace vl {

class PauseMenu {
public:
    PauseMenu(float width, float height);
    ~PauseMenu() = default;

    void draw(sf::RenderWindow& window);
    void moveUp();
    void moveDown();
    int getSelectedOption() const;

private:
    int selectedOptionIndex;
    sf::Font font;
    std::vector<sf::Text> PauseMenuOptions;
};

} // namespace vl

#endif // MENU_HPP
