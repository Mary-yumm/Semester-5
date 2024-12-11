#include<iostream>
#include "utilities.h"
using namespace std;


bool isEqual(char *str1, char *str2)
{
    int i = 0;

    while (str1[i] != '\0' && str2[i] != '\0')
    {
        if (str1[i] != str2[i])
        {
            return false;
        }
        i++;
    }
    if (str1[i] != '\0' || str2[i] != '\0')
    {
        return false;
    }
    else
    {
        return true;
    }
}


double stringToDouble(char *str)
{
    double result = 0.0;
    int i = 0;
    // Process the integer part
    while (str[i] >= '0' && str[i] <= '9')
    {
        result = result * 10 + (str[i] - '0');
        i++;
    }

    // Process the decimal part if present
    if (str[i] == '.')
    {
        i++;
        double decimalPlace = 0.1;
        while (str[i] >= '0' && str[i] <= '9')
        {
            result += (str[i] - '0') * decimalPlace;
            decimalPlace *= 0.1;
            i++;
        }
    }

    return result;
}
