#include <iostream>
#include <iomanip>
#include <cstdlib>
using namespace std;
void hangman(int);
int main()
{
    char list[100], choose;
    char gword[50], guess;
    bool flag = 0;
    cout << "Enter a list of words separated by commas: ";
    cin.get(list, 100);
    int i = 0, word = 1, j, k = 1, a[10], o = 0;
    int gchars;
    // counting the number of words
    // Also the positions of i when each comma appears
    while (list[i] != '\0')
    {
        if (list[i] == ',')
        {
            word++;
            a[k] = i; // a[0]=2 a[1]=7
            k++;      // 1 2
        }
        i++; // 3 8
    }
    // prinitng num of words
    // cout<<"words = "<<word<<endl;
    // printing positions of i array
    //    cout<<"Positions of i when each comma appears: ";
    //    for(i=0;i<k;i++)
    //    cout<<a[i]<<" ";
    //    cout<<endl;
    // generating numbers for random words
    srand(time(0));
    int choice = rand() % word + 1;
    // cout << "choice = " << choice << endl;
    choice = a[choice - 1];
    if (choice != 0)
        choice++;
    // cout << "subscript when that word starts = " << choice << endl;
    j = 0;
    // creating a new array in which random word is stored
    while (list[choice] != '\0' && list[choice] != ',')
    {
        gword[j] = list[choice];
        choice++;
        j++;
    }
    gword[j]='\0';
    // Printing that random word
    gchars = 0;
    while (gword[gchars] != '\0')
    {
        // cout<<gword[gchars];
        gchars++;
    }
    cout << endl;
    char up[gchars];
    for (j = 0; j < gchars; j++)
    {
        up[j] = '_';
    }
    int turns = 1, h = 0, win = 1;
    // loop
    hangman(h);
    while (h < 6)
    {
        win = 1;
        flag = 0;
        cout << endl;
        for (i = 0; i < gchars; i++)
        {
            cout << up[i] << " ";
            if (up[i] == '_')
                win = 0;
        }
        if (win == 1)
            break;
        cout << "\n\nEnter your " << turns << " guess: ";
        cin >> guess;
        for (i = 0; i < gchars; i++)
        {
            if (guess == gword[i])
            {
                flag = 1;
                up[i] = guess;
            }
        }
        if (flag != 1)
        {
            cout << "Wrong guess!" << endl;
            h++;
            hangman(h);
            if(h==6){
                cout << "Game Over! The word was " << gword << endl;
            }
        }
        turns++;
        if (h == 5)
        {
            if (o == 0)
            {
                cout << "Enter h to get a hint! ";
                cin >> choose;
                int hintsize = 0;
                if (choose == 'h' && o == 0)
                {
                    j = 0;
                    for (i = 0; i < gchars; i++)
                        if (up[i] == '_')
                            hintsize++;
                    // cout << "Hint index " << hintsize << endl;
                    char hint[hintsize];
                    // cout << "Hint array: ";
                    for (i = 0; i < gchars; i++)
                    {
                        if (up[i] == '_')
                        {
                            hint[j] = gword[i];
                            // cout << hint[j] << " ";
                            j++;
                        }
                    }
                    int random = rand() % hintsize;
                    // cout << "\nRandom = " << random << endl;
                    for (i = 0; i < gchars; i++)
                    {
                        if (gword[i] == hint[random])
                        {
                            up[i] = hint[random];
                            cout << up[i] << endl;
                        }
                    }
                    o = 1;
                }
            }
        }
    }
    if (win == 1)
    {
        h = 7;
        hangman(h);
    }
    return 0;
}
void hangman(int h)
{
    int i;
    cout << endl;
    if (h == 0)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl;
        for (i = 0; i < 4; i++)
            cout << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 1)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        for (i = 0; i < 3; i++)
            cout << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 2)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        cout << setw(9) << "|" << setw(4) << "|" << endl;
        for (i = 0; i < 2; i++)
            cout << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 3)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        cout << setw(8) << "/"
             << "|" << setw(4) << "|" << endl;
        for (i = 0; i < 2; i++)
            cout << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 4)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        cout << setw(8) << "/"
             << "|"
             << "\\" << setw(3) << "|" << endl;
        for (i = 0; i < 2; i++)
            cout << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 5)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        cout << setw(8) << "/"
             << "|"
             << "\\" << setw(3) << "|" << endl
             << setw(8) << "/" << setw(5) << "|" << endl
             << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
    }
    else if (h == 6)
    {
        cout << "-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(9) << "O" << setw(4) << "|" << endl;
        cout << setw(8) << "/"
             << "|"
             << "\\" << setw(3) << "|" << endl
             << setw(8) << "/" << setw(2) << "\\" << setw(3) << "|" << endl
             << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
        cout << "You lost the game :(" << endl;
    }
    else if (h == 7)
    {
        cout << endl;
        cout << "\n-------HANGMAN-------" << endl;
        cout << setw(13) << "+---+" << endl
             << setw(9) << "|" << setw(4) << "|" << endl
             << setw(8) << "\\"
             << "O"
             << "/" << setw(3) << "|" << endl;
        cout << setw(9) << "|" << setw(4) << "|" << endl
             << setw(8) << "/" << setw(2) << "\\" << setw(3) << "|" << endl
             << setw(13) << "|" << endl;
        cout << setw(7);
        for (i = 0; i < 9; i++)
            cout << "=";
        cout << endl;
        cout << setw(19) << "BRAVO! YOU WON!" << endl;
    }
}