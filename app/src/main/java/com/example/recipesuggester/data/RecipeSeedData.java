package com.example.recipesuggester.data;

import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;

import java.util.Arrays;
import java.util.List;

// the starting recipe collection. this list only gets used ONCE, to fill the database the
// first time the app runs (see DatabaseHelper). after that everything is read from SQLite.
//
// rules I'm sticking to so the matching works later:
//  - ingredient names are lowercase and singular ("egg" not "Eggs")
//  - units are only g, kg, ml, l or pcs (same as the pantry form)
public class RecipeSeedData {

    public static List<Recipe> getRecipes() {
        return Arrays.asList(
                recipe("Scrambled Eggs",
                        "Soft, creamy scrambled eggs ready in 5 minutes.",
                        "Crack the eggs into a bowl, add the milk and salt and whisk.\n"
                                + "Melt the butter in a pan on low heat.\n"
                                + "Pour in the eggs and stir slowly until just set.\n"
                                + "Take off the heat and serve straight away.",
                        ing("egg", 3, "pcs"), ing("milk", 50, "ml"), ing("butter", 10, "g"),
                        ing("salt", 2, "g")),

                recipe("Pancakes",
                        "Classic fluffy pancakes for breakfast.",
                        "Mix the flour and sugar in a bowl.\n"
                                + "Whisk in the eggs and milk until smooth.\n"
                                + "Melt a little butter in a pan on medium heat.\n"
                                + "Pour in a small ladle of batter and cook until bubbles form, then flip.\n"
                                + "Repeat with the rest of the batter.",
                        ing("flour", 200, "g"), ing("milk", 300, "ml"), ing("egg", 2, "pcs"),
                        ing("sugar", 25, "g"), ing("butter", 20, "g")),

                recipe("French Toast",
                        "Golden egg-dipped toast, sweet and quick.",
                        "Whisk the eggs, milk and sugar together in a flat dish.\n"
                                + "Soak each slice of bread in the mixture for a few seconds per side.\n"
                                + "Fry in butter on medium heat until golden on both sides.",
                        ing("bread", 4, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"),
                        ing("butter", 15, "g"), ing("sugar", 10, "g")),

                recipe("Cheese Omelette",
                        "A simple folded omelette with melted cheese.",
                        "Whisk the eggs with the salt.\n"
                                + "Melt the butter in a pan and pour in the eggs.\n"
                                + "When the bottom has set, sprinkle the grated cheese over half.\n"
                                + "Fold the omelette over and cook for one more minute.",
                        ing("egg", 3, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"),
                        ing("salt", 2, "g")),

                recipe("Tomato Pasta",
                        "Pasta in a quick fresh tomato and garlic sauce.",
                        "Cook the pasta in salted boiling water, then drain.\n"
                                + "Chop the tomatoes and crush the garlic.\n"
                                + "Fry the garlic in the oil for 1 minute, add the tomatoes and simmer for 10 minutes.\n"
                                + "Mix the sauce through the pasta.",
                        ing("pasta", 200, "g"), ing("tomato", 4, "pcs"), ing("garlic", 2, "pcs"),
                        ing("oil", 30, "ml"), ing("salt", 5, "g")),

                recipe("Garlic Butter Rice",
                        "Fluffy rice with garlic and butter, a good side dish.",
                        "Rinse the rice until the water runs clear.\n"
                                + "Melt the butter in a pot and fry the chopped garlic for 1 minute.\n"
                                + "Add the rice, salt and double the amount of water.\n"
                                + "Cover and simmer on low for 15 minutes, then fluff with a fork.",
                        ing("rice", 200, "g"), ing("butter", 30, "g"), ing("garlic", 3, "pcs"),
                        ing("salt", 3, "g")),

                recipe("Mashed Potatoes",
                        "Smooth, buttery mashed potatoes.",
                        "Peel and cut the potatoes into chunks.\n"
                                + "Boil in salted water for about 20 minutes until soft.\n"
                                + "Drain, then mash with the butter and warm milk until smooth.",
                        ing("potato", 4, "pcs"), ing("butter", 40, "g"), ing("milk", 100, "ml"),
                        ing("salt", 3, "g")),

                recipe("Roast Potatoes",
                        "Crispy oven-roasted potatoes with garlic.",
                        "Heat the oven to 200 degrees C.\n"
                                + "Cut the potatoes into chunks and boil for 8 minutes, then drain.\n"
                                + "Toss with the oil, salt and crushed garlic.\n"
                                + "Roast for 40 minutes, turning halfway, until crispy.",
                        ing("potato", 6, "pcs"), ing("oil", 45, "ml"), ing("salt", 5, "g"),
                        ing("garlic", 2, "pcs")),

                recipe("Chicken Stir Fry",
                        "Quick chicken and vegetable stir fry.",
                        "Slice the chicken, pepper, onion and carrots into thin strips.\n"
                                + "Heat the oil in a pan on high heat and fry the chicken until cooked.\n"
                                + "Add the vegetables and fry for 4 minutes.\n"
                                + "Add the soy sauce, toss and serve.",
                        ing("chicken breast", 2, "pcs"), ing("bell pepper", 1, "pcs"),
                        ing("onion", 1, "pcs"), ing("carrot", 2, "pcs"), ing("oil", 30, "ml"),
                        ing("soy sauce", 30, "ml")),

                recipe("Chicken Fried Rice",
                        "A good way to use up leftover rice and chicken.",
                        "Cook the rice and let it cool (leftover rice works best).\n"
                                + "Fry the diced chicken and chopped onion in the oil until cooked.\n"
                                + "Push to one side, scramble the eggs in the pan.\n"
                                + "Add the rice and soy sauce and stir fry for 3 minutes.",
                        ing("rice", 200, "g"), ing("chicken breast", 1, "pcs"), ing("egg", 2, "pcs"),
                        ing("onion", 1, "pcs"), ing("soy sauce", 30, "ml"), ing("oil", 30, "ml")),

                recipe("Spaghetti Bolognese",
                        "Pasta with a rich beef and tomato sauce.",
                        "Fry the chopped onion and garlic in the oil until soft.\n"
                                + "Add the mince and cook until browned.\n"
                                + "Add the chopped tomatoes and simmer for 20 minutes.\n"
                                + "Cook the pasta, drain and serve with the sauce on top.",
                        ing("pasta", 250, "g"), ing("minced beef", 500, "g"), ing("onion", 1, "pcs"),
                        ing("tomato", 4, "pcs"), ing("garlic", 2, "pcs"), ing("oil", 15, "ml")),

                recipe("Beef Burgers",
                        "Homemade beef burgers in bread rolls.",
                        "Grate the onion and mix it with the mince, egg and salt.\n"
                                + "Shape into 4 patties.\n"
                                + "Fry or grill for about 5 minutes per side.\n"
                                + "Serve in the bread rolls.",
                        ing("minced beef", 500, "g"), ing("onion", 1, "pcs"), ing("egg", 1, "pcs"),
                        ing("bread roll", 4, "pcs"), ing("salt", 3, "g")),

                recipe("Tuna Pasta Salad",
                        "Cold pasta salad with tuna, good for lunch boxes.",
                        "Cook the pasta, drain and rinse under cold water.\n"
                                + "Drain the tuna and dice the cucumber.\n"
                                + "Mix everything with the mayonnaise.\n"
                                + "Keep in the fridge until serving.",
                        ing("pasta", 200, "g"), ing("tuna", 1, "pcs"), ing("cucumber", 1, "pcs"),
                        ing("mayonnaise", 60, "g")),

                recipe("Greek Salad",
                        "Fresh salad with tomato, cucumber and feta.",
                        "Chop the tomatoes and cucumber into chunks.\n"
                                + "Slice the onion thinly.\n"
                                + "Put everything in a bowl and crumble the feta on top.\n"
                                + "Drizzle with the oil.",
                        ing("tomato", 3, "pcs"), ing("cucumber", 1, "pcs"), ing("onion", 1, "pcs"),
                        ing("feta cheese", 100, "g"), ing("oil", 30, "ml")),

                recipe("Vegetable Soup",
                        "A warm, simple soup to use up vegetables.",
                        "Chop all the vegetables into small pieces.\n"
                                + "Fry the onion in the oil for 3 minutes.\n"
                                + "Add the rest of the vegetables and cover with water.\n"
                                + "Add the salt and simmer for 25 minutes, then blend if you want it smooth.",
                        ing("potato", 2, "pcs"), ing("carrot", 3, "pcs"), ing("onion", 1, "pcs"),
                        ing("tomato", 2, "pcs"), ing("oil", 15, "ml"), ing("salt", 5, "g")),

                recipe("Banana Smoothie",
                        "Thick banana smoothie, uses up ripe bananas.",
                        "Peel the bananas.\n"
                                + "Put everything in a blender.\n"
                                + "Blend until smooth and serve cold.",
                        ing("banana", 2, "pcs"), ing("milk", 250, "ml"), ing("yoghurt", 150, "g"),
                        ing("honey", 15, "ml")),

                recipe("Overnight Oats",
                        "No-cook oats you make the night before.",
                        "Mix the oats, milk and yoghurt in a jar.\n"
                                + "Stir in the honey.\n"
                                + "Leave in the fridge overnight.\n"
                                + "Slice the banana on top in the morning.",
                        ing("oats", 80, "g"), ing("milk", 150, "ml"), ing("yoghurt", 100, "g"),
                        ing("honey", 15, "ml"), ing("banana", 1, "pcs")),

                recipe("Banana Bread",
                        "Moist banana bread, great for overripe bananas.",
                        "Heat the oven to 180 degrees C and grease a loaf tin.\n"
                                + "Mash the bananas and mix in the melted butter and sugar.\n"
                                + "Beat in the eggs.\n"
                                + "Fold in the flour and baking powder.\n"
                                + "Bake for 55 minutes until a skewer comes out clean.",
                        ing("banana", 3, "pcs"), ing("flour", 250, "g"), ing("sugar", 100, "g"),
                        ing("butter", 80, "g"), ing("egg", 2, "pcs"), ing("baking powder", 10, "g")),

                recipe("Mushroom Risotto",
                        "Creamy rice cooked slowly with mushrooms.",
                        "Fry the chopped onion and sliced mushrooms in the butter.\n"
                                + "Add the rice and stir for 1 minute.\n"
                                + "Add the hot stock a ladle at a time, stirring until each ladle is absorbed.\n"
                                + "After about 20 minutes, stir in the grated cheese.",
                        ing("rice", 250, "g"), ing("mushroom", 250, "g"), ing("onion", 1, "pcs"),
                        ing("butter", 30, "g"), ing("cheese", 50, "g"), ing("stock", 1, "l")),

                recipe("Cheesy Baked Potatoes",
                        "Oven baked potatoes filled with butter and cheese.",
                        "Heat the oven to 200 degrees C.\n"
                                + "Prick the potatoes with a fork and bake for 1 hour.\n"
                                + "Cut open, add the butter and salt and top with the grated cheese.\n"
                                + "Put back in the oven for 5 minutes to melt the cheese.",
                        ing("potato", 4, "pcs"), ing("cheese", 100, "g"), ing("butter", 30, "g"),
                        ing("salt", 2, "g")),

                recipe("Lemon Garlic Chicken",
                        "Pan-fried chicken with lemon and garlic.",
                        "Rub the chicken with the salt, crushed garlic and lemon juice.\n"
                                + "Heat the oil in a pan on medium heat.\n"
                                + "Fry the chicken for 6 to 7 minutes per side until cooked through.\n"
                                + "Rest for 5 minutes before slicing.",
                        ing("chicken breast", 2, "pcs"), ing("lemon", 1, "pcs"), ing("garlic", 3, "pcs"),
                        ing("oil", 30, "ml"), ing("salt", 3, "g")),

                recipe("Toasted Cheese Sandwich",
                        "A crispy, melty toasted cheese sandwich.",
                        "Butter one side of each slice of bread.\n"
                                + "Put the cheese between the slices, buttered sides out.\n"
                                + "Toast in a pan on medium heat for 3 minutes per side until golden.",
                        ing("bread", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 15, "g"))
        );
    }

    // little helpers so the list above is easier to read
    private static Recipe recipe(String name, String description, String instructions,
                                 RecipeIngredient... ingredients) {
        return new Recipe(0, name, description, instructions, Arrays.asList(ingredients));
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }
}
