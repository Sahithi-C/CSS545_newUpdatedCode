package com.example.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.repository.RecipeRepository;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchViewModel extends AndroidViewModel {
    private final RecipeRepository repository;
    private LiveData<List<String>> allRecipeTitles;

    public SearchViewModel(Application application) {
        super(application);
        repository = new RecipeRepository(application);
        allRecipeTitles = repository.getAllRecipeTitles();
    }

    public LiveData<List<String>> getAllRecipeTitles() {
        return allRecipeTitles;
    }

    public LiveData<List<String>> searchRecipes(String query) {
        return Transformations.map(allRecipeTitles, titles ->
                titles.stream()
                        .filter(title -> title.toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList())
        );
    }

    public void insertSampleData() {
        // Recipe 1: Biryani
        repository.insertRecipe(
                "Biryani",
                "recipe_paneer_biryani",
                "90 minutes",
                "Medium ~ High",
                Arrays.asList(
                        "1 big Onion",
                        "2 big tomatoes (puree)",
                        "250 gms Rice",
                        "2 tbsp Curd",
                        "1 tbsp Ginger-Garlic paste"
                ),
                Arrays.asList(
                        "2 cups of vegetables (carrot, bell pepper, cauliflower, peas, beans)",
                        "cottage cheese (paneer)"
                ),
                Arrays.asList(
                        "Cook rice until al dente (Basmati rice recommended).",
                        "Heat 4 tbsp of oil in a pan.",
                        "Add whole spices: cumin seeds, bay leaf, cardamom, cloves, cinnamon stick, star anise.",
                        "Add julienned onions and cook until golden brown.",
                        "Lower heat, add ginger-garlic paste, and ground spices. Cook for 2 mins.",
                        "(Optional) Add vegetables and cook for 5-7 mins.",
                        "Add curd and let cook for 5 mins.",
                        "Add tomato puree, salt, and kasuri methi. Cook until water evaporates.",
                        "(Optional) Add paneer cubes, lightly pan-fried.",
                        "Add 1/2 glass of water.",
                        "Add rice on top, garnish, and steam for 10-15 mins."
                )
        );

        // Recipe 2: Paneer Butter Masala
        repository.insertRecipe(
                "Paneer Butter Masala",
                "recipe_paneer_butter_masala",
                "60 minutes",
                "Medium",
                Arrays.asList(
                        "250g Paneer",
                        "2 Onions",
                        "3 Tomatoes",
                        "2 tbsp Butter",
                        "1 tbsp Cream"
                ),
                Arrays.asList(
                        "Kasuri Methi",
                        "Coriander leaves for garnishing"
                ),
                Arrays.asList(
                        "Cut paneer into cubes and soak in warm water.",
                        "Sauté onions until golden brown.",
                        "Add tomatoes and cook until soft.",
                        "Blend the mixture into a smooth paste.",
                        "Heat butter in a pan and add the paste.",
                        "Add spices and cook for 5 minutes.",
                        "Add paneer cubes and simmer for 5 minutes.",
                        "Add cream and garnish with coriander leaves."
                )
        );

        // Recipe 3: Peanut Chutney
        repository.insertRecipe(
                "Peanut Chutney",
                "recipe_peanut_chutney",
                "10 minutes",
                "Medium ~ High",
                Arrays.asList(
                        "Half julienned small onion",
                        "1 small cup peanuts",
                        "2 garlic",
                        "3 ~ 4 finely chopped green chili",
                        "0.5 grams tamarind (1 small petal of tamarind)",
                        "Water",
                        "1 tbsp salt"
                ),
                Arrays.asList(
                        "6 ~ 7 curry leaves",
                        "Half tbsp mustard seeds",
                        "2 dry red chillies",
                        "Half tbsp mustard seeds",
                        "One-fourth tbsp chili powder"
                ),
                Arrays.asList(
                        "Add 2 tbsp of oil to a pan, keep the heat on medium.",
                        "When the oil is hot add garlic, onions to the pan.",
                        "Add peanuts to the pan when onions turn golden brown.",
                        "Fry the ingredients until the peanuts are roasted or you can hear crackling sound of peanuts, turn off the heat and let the ingredients cool down.",
                        "Move the ingredients to a grinder, add turmeric, salt, green chili, dry red chili powder(if tolerant to spice), add  1 small cup of water and grind the ingredients until fine paste(can add more water if required more runny consistency).",
                        "Your chutney mixture is ready(it can be served as it is or add a tempering to it for a crunchy flavour).",
                        "For the tempering heat 1 tbsp of oil in a small sauce pan, add mustard seeds, curry leaves, dry red chili.",
                        "When the mustard seeds starts to crackle add them to the chutney mixture, mix the ingredients.",
                        "Serve the chutney with roti, bread, dosa, idly and enjoy!"
                )
        );

        // Recipe 4: Okra Fry Curry
        repository.insertRecipe(
                "Okra Fry Curry",
                "recipe_okra_fry_curry",
                "30 minutes",
                "Medium",
                Arrays.asList(
                        "Half roughly chopped small onion",
                        "200grams cut okra",
                        "5 ~ 6 chopped garlic",
                        "6 ~ 7 curry leaves",
                        "Half tbsp mustard seeds",
                        "Half tbsp chana dal",
                        "Half tbsp turmeric powder",
                        "Half ~ 1 tbsp dry red chili powder",
                        "1 tbsp salt",
                        "1 tbsp fresh or dry coconut powder"
                ),
                Arrays.asList(
                        "Half tbsp urad dal",
                        "2 finely chopped green chili",
                        "1 tbsp roasted peanuts",
                        "Half tbsp coriander powder"
                ),
                Arrays.asList(
                        "First shallow fry the Okra for 5 mins on high heat to remove its stickiness. And keep the fried okra a side",
                        "Now, heat a pan with 1 to 2 tbsp of oil on medium heat.",
                        "Add mustard seeds, urad dal, chana dal into the oil.",
                        "When the mustard seeds start crackling, add onions, cut green chili, and curry leaves to the pan.",
                        "Shallow fry the onions for 2 minutes and add shallow fried okra, turmeric to the pan.",
                        "After stirring and mixing all the ingredients add salt, dry red chili powder, coriander powder to the ingredients and mix them.",
                        "When the okra is tender and cooked switch off the heat and add dry coconut powder and roasted peanuts to pan and mix the ingredients.",
                        "Serve the fresh Okra fry curry hot with rice or roti!"
                )
        );

        // Recipe 5: Veg Hakka Noodles
        repository.insertRecipe(
                "Veg Hakka Noodles",
                "recipe_veg_hakka_noodles",
                "25 minutes",
                "Medium",
                Arrays.asList(
                        "1 small cup julienned onions",
                        "1 small cup julienned cabbage",
                        "1 tbsp garlic",
                        "1 tbsp ginger",
                        "1 ~ 2 tbsp soy sauce(light / dark)",
                        "2 packs of noodles(any flour noodles)",
                        "1 tbsp salt",
                        "1 tbsp pepper"
                ),
                Arrays.asList(
                        "2 finely chopped green chili",
                        "1 small cup julienned carrots",
                        "1 small cup julienned bell pepper",
                        "Half tbsp spring onion greens"
                ),
                Arrays.asList(
                        "Boil water in a large bowl for cooking noodles.",
                        "When the water starts boiling add 1 tbsp salt and 2 packs of noodles to it and cook the noodles aldente.",
                        "Remove the noodles from cooking pot and wash with cold water to stop the cooking process and coat the noodles with 1 ~ 2 tbsp of oil. And them aside",
                        "Heat a pan with 3 ~ 4 tbsp of oil on high heat.",
                        "Add ginger, garlic, green chili to the pan and sauté them for 30 seconds.",
                        "Add julienned onions, cabbage, bell pepper, carrots to the pan, add salt and stir fry them on high heat(don't cook until tender just cook for 4 mins on high heat, the vegetables should maintain its crispiness).",
                        "Add cooked noodles, soy sauce, pepper to the pan and mix the ingredients(try not to break the noodles).",
                        "Garnish the noodles with spring onion greens and serve hot!"
                )
        );

        // Recipe 6: Upma
        repository.insertRecipe(
                "Upma",
                "recipe_upma",
                "20 minutes",
                "Low",
                Arrays.asList(
                        "Half roughly chopped small onion",
                        "6 ~ 7 curry leaves",
                        "Half tbsp mustard seeds",
                        "Half tbsp chana dal",
                        "2 ~ 3 finely chopped green chili",
                        "1 roughly chopped tomato",
                        "1 cup semolina",
                        "3 cups water",
                        "1 tbsp salt"
                ),
                Arrays.asList(
                        "1 tbsp peanuts",
                        "Half tbsp urad dal",
                        "1 tbsp finely chopped ginger",
                        "Half finely chopped carrot(1 small carrot)",
                        "4 ~ 5 finely chopped beans",
                        "Half finely chopped potato(1 small potato)",
                        "1 ~ 2 tbsp peas",
                        "Half tbsp finely chopped cilantro"
                ),
                Arrays.asList(
                        "Heat a pan with 1 to 2 tbsp of oil on medium heat.",
                        "Add mustard seeds, chana dal, urad dal, and ginger to the pan.",
                        "When the mustard seeds start to splutter add onions, green chili, curry leaves to the pan and shallow fry them until onions turn translucent.",
                        "Add tomato, carrots, beans, potatoes, peas the pan, reduce the heat to low and close the lid for pan and cooks the vegetables until tender.",
                        "After vegetables are cooked add salt and 3 cups of water to the pan, increase the heat to medium and wait until water starts to boil.",
                        "When water starts to boil add 1 cup of semolina to the pan and continuously stir(for no lumps), until all ingredients are mixed well and semolina absorbs all the water.",
                        "Decrease the heat to low, close the lid and let it cook on low flame for 3 ~ 4 mins.",
                        "Switch off the heat, add cilantro, mix the ingredients well and serve hot!"
                )
        );
    }
}