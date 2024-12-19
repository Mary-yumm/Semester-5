// Menu.hpp
#ifndef MENU_HPP
#define MENU_HPP

#include <SFML/Graphics.hpp>
#include <vector>
#include <string>

namespace vl
{
    /**
     * @class Menu
     * @brief A class that manages the game's menu and its interactions.
     *
     * This class is responsible for handling the menu display and user navigation
     * between menu options. It allows the user to select different options in the
     * menu and provides the ability to draw the menu on the screen.
     */

    class Menu
    {
    public:
        /**
         * @brief Construct a new Menu object.
         *
         * Initializes the menu with the specified window dimensions and loads
         * the necessary font for displaying menu options.
         *
         * @param width The width of the window.
         * @param height The height of the window.
         */
        Menu(float width, float height);
        /**
         * @brief Destroy the Menu object.
         *
         * The destructor is default, as no dynamic memory management is done
         * explicitly in the Menu class.
         */
        ~Menu() = default;

        /**
         * @brief Draw the menu on the provided window.
         *
         * This method renders all the menu options on the screen using the
         * specified window object.
         *
         * @param window The window on which the menu is drawn.
         */
        void draw(sf::RenderWindow &window);

        /**
         * @brief Move the selection up in the menu.
         *
         * Changes the selected option to the one above the current selection,
         * wrapping around if necessary.
         */
        void moveUp();
        /**
         * @brief Move the selection down in the menu.
         *
         * Changes the selected option to the one below the current selection,
         * wrapping around if necessary.
         */
        void moveDown();
        /**
         * @brief Get the index of the currently selected option.
         *
         * @return The index of the selected menu option.
         */
        int getSelectedOption() const;

    private:
        int selectedOptionIndex;           /**< Index of the currently selected option. */
        sf::Font font;                     /**< Font used for rendering the menu options. */
        std::vector<sf::Text> menuOptions; /**< List of menu options displayed on the screen. */
    };

} // namespace vl

#endif // MENU_HPP
