#ifndef PAUSEMENU_HPP
#define PAUSEMENU_HPP

#include <SFML/Graphics.hpp>
#include <vector>
#include <string>

namespace vl {

/**
 * @class PauseMenu
 * @brief Represents a pause menu in the game.
 * 
 * The `PauseMenu` class manages the menu options that appear when the game is paused. 
 * It allows the player to navigate through different options such as resuming the game, 
 * changing settings, or exiting the game.
 */
class PauseMenu {
public:
    /**
     * @brief Construct a new PauseMenu object.
     * 
     * Initializes the pause menu with the specified window dimensions and loads 
     * the font required for rendering the menu options.
     * 
     * @param width The width of the window.
     * @param height The height of the window.
     */
    PauseMenu(float width, float height);

    /**
     * @brief Destroy the PauseMenu object.
     * 
     * The destructor is the default one as no dynamic memory allocation is done.
     */
    ~PauseMenu() = default;

    /**
     * @brief Draw the pause menu on the provided window.
     * 
     * Renders all the pause menu options on the screen using the specified window object.
     * 
     * @param window The window on which the pause menu is drawn.
     */
    void draw(sf::RenderWindow& window);

    /**
     * @brief Move the selection up in the pause menu.
     * 
     * Changes the selected option to the one above the current selection, 
     * and updates the color of the selected option to indicate selection.
     */
    void moveUp();

    /**
     * @brief Move the selection down in the pause menu.
     * 
     * Changes the selected option to the one below the current selection, 
     * and updates the color of the selected option to indicate selection.
     */
    void moveDown();

    /**
     * @brief Get the index of the currently selected option in the pause menu.
     * 
     * @return The index of the selected pause menu option.
     */
    int getSelectedOption() const;

private:
    int selectedOptionIndex; ///< The index of the currently selected option in the pause menu.
    sf::Font font; ///< The font used for the menu text.
    std::vector<sf::Text> PauseMenuOptions; ///< List of menu options displayed in the pause menu.
};

} // namespace vl

#endif // PAUSEMENU_HPP
