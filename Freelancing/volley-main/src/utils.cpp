#include <SFML/Graphics.hpp>
#include <cmath>

namespace vl {
  namespace utils {

    /**
     * @brief Calculate the squared distance between two points.
     * 
     * This function computes the squared Euclidean distance between two
     * 2D points represented by sf::Vector2f. The result is the square of
     * the actual distance, which is computationally cheaper than calculating
     * the distance directly.
     * 
     * @param a The first point.
     * @param b The second point.
     * 
     * @return The squared distance between points a and b.
     */
    float sd(const sf::Vector2f& a, const sf::Vector2f& b) {
      auto x = a.x - b.x;
      auto y = a.y - b.y;
      return x*x + y*y;
    }

    /**
     * @brief Build a vector from two points.
     * 
     * This function creates a vector by subtracting the coordinates of
     * the second point from the first point. It returns the resulting vector.
     * 
     * @param a The starting point.
     * @param b The ending point.
     * 
     * @return The vector from point a to point b.
     */
    sf::Vector2f v(const sf::Vector2f& a, const sf::Vector2f& b) {
      return b - a;
    }

    /**
     * @brief Build a normalized vector from two points.
     * 
     * This function creates a vector from two points and normalizes it
     * to have a length of 1. The normalization is achieved by dividing the
     * vector by its own magnitude (calculated using the hypotenuse).
     * 
     * @param a The starting point.
     * @param b The ending point.
     * 
     * @return A normalized vector from point a to point b.
     */
    sf::Vector2f nv(const sf::Vector2f& a, const sf::Vector2f& b) {
      auto u = v(b,a);
      auto n = std::hypotf(u.x, u.y);

      return u/n;
    }
  }
}
