package com.example.pool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pool.dto.Chemical
import com.example.pool.dto.Algae
import com.example.pool.dto.Pool
import com.example.pool.ui.main.MainViewModel

//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //create a test Pool object. In the future this will be created from user input
    val testPool = Pool("pool1", 15000F)
    //set this pool object to be the active pool
    val activePool = testPool

    //each chemical can be created here using the chemical class
    val chlorine = Chemical(name= "chl", okRange= arrayOf(1F, 5F), hoursCantSwim= 8F,
            ozPerGallon= .005F, ASINTiers= arrayOf("B096N1N5DJ", "B00PZZFG0O", "B08QMW3XJV"))

    val alkalinity = Chemical(name= "alk", okRange= arrayOf(80F, 120F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("B076KSBF69", "B0774M73SF", "B073H1NJKK"))

    val calciumHardness = Chemical(name= "cal", okRange= arrayOf(200F, 300F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("B000UVQUJ4", "B084GQH8YF", "B07QXTNV1B"))

    val pH = Chemical(name= "pH", okRange= arrayOf(7.4F, 7.6F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("B084GPWRBL", "B08PG4C2NQ", "B004WDVT6K","B084GPS6KR", "B077715Y9L", "B07YZPNWDL"))

    val cyanuricAcid = Chemical(name= "cya", okRange= arrayOf(30F, 100F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("B00TNWGZE6", "B011AFBUTI", "B07FPZP6ZX"))
    
    val totalDissolvedSolids = Chemical(name= "tds", okRange= arrayOf(0F, 1500F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("N/A"))

    val phosphates = Chemical(name= "pho", okRange= arrayOf(0F, 100F), hoursCantSwim= 0F,
            ozPerGallon= .005F, ASINTiers= arrayOf("N/A"))

    val gAlgae = Algae(type= "Green", hoursCantSwim= 0F, ozPerGallon= 0F, chlBoostPerGallon= 0F, ASINTag = "B002WKJAYS")
    val yAlgae = Algae(type= "Yellow", hoursCantSwim= 0F, ozPerGallon= 0F, chlBoostPerGallon= 0F, ASINTag = "B01LW1QNZ7")
    val bAlgae = Algae(type= "Black", hoursCantSwim= 0F, ozPerGallon= 0F, chlBoostPerGallon= 0F, ASINTag = "B00BGNLPCW")
    val myProduct = MainViewModel().fetchProduct(myASIN="B00PZZFG0O")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TODO(
        //val exampleList  = generatePoolStatusList(2)
        //recycler_view.adapter = PoolItemAdapter(exampleList)
        //recycler_view.layoutManager = LinearLayoutManager(this)
        //recycler_view.setHasFixedSize(true)
        )
    }

    private fun generatePoolStatusList(size: Int) : List<poolStatusItem> {
        TODO()
        val list = ArrayList<poolStatusItem>()

        val item = poolStatusItem(imageResource = 1, "Test", "Low", "0")
        list += item

        return list
    }

    /**
     * @param priceLevel is a value 0 (low), 1 (medium), or 2 (high) which corresponds to an index in the chemical arrays. In the
     * case of algaecides, priceLevel will refer to the specific type of algae detected. The index for algae reads as thus: 0 (green), 1 (yellow)
     * or 2 (black.)
     * @param productType is the chemical name ("chlorine", "cyanuricAcid", "phIncrease", "phDecrease", "algaecide", "calcium", "sodiumBicarbonate")
     * @return the asin of the product which we will fetch from the amazon api
     */
    private fun getASIN(priceLevel: Int, productType: String) : String {
        when (productType) {
            "chlorine" -> return chlorine.ASINTiers[priceLevel]
            //5 lbs
            "cyanuricAcid" -> return cyanuricAcid.ASINTiers[priceLevel]
            //4 lbs
            "phIncrease" -> return pH.ASINTiers[priceLevel]
            //5 lbs for most
            "phDecrease" -> return pH.ASINTiers[priceLevel+3]
            //1 qt
            "greenAlgaecide" -> return gAlgae.ASINTag
            //1 qt
            "yellowAlgaecide" -> return yAlgae.ASINTag
            //1 qt
            "blackAlgaecide" -> return bAlgae.ASINTag
            //4 lbs
            "calcium" -> return calciumHardness.ASINTiers[priceLevel]
            //5 lbs
            "sodiumBicarbonate" -> return alkalinity.ASINTiers[priceLevel]
            else -> return ""
        }
    }

    /**
     * Given any pool and any algae type, return the amount of algaecide and chlorine to add,
     * and the amount of time needed to make the pool safe
     */
    private fun cleanAlgae(pool: Pool, algae: Algae): String {
        val algaecideNeeded = algae.ozPerGallon * pool.poolGallonSize
        val chlorineNeeded = algae.chlBoostPerGallon * pool.poolGallonSize
        val hoursPoolNotSafe = Math.max(algae.hoursCantSwim, chlorine.hoursCantSwim)

        return algae.toString() + " should never appear, and any amount is dangerous. " +
                algaecideNeeded + " ounces of algecide and " +
                chlorineNeeded + " ounces of chlorine is necessary. The pool is not safe until " +
                hoursPoolNotSafe + " after use."
    }

    /**
     * Given any pool and chemical, and a current reading of that chemical's concentration in the pool,
     * return the action to take (add, remove, or none)
     * @param chemConcentration the proportion of the pool water that is made up of this chemical,
     * expressed as a decimal between 0-1
     */
    private fun testChemicalConcentration(pool: Pool, chemical: Chemical, chemConcentration: Float): String {
        val recommended = chemical.ozPerGallon
        val name = chemical.name
        val hoursNotSafe = chemical.hoursCantSwim
        when {
            recommended === chemConcentration -> {
                return "$name at optimal levels. No adjustment needed!"
            }
            chemConcentration < recommended -> {
                val diffProportion = recommended - chemConcentration
                val diffAmount = diffProportion * pool.poolGallonSize
                return "There is not enough $name in the pool. Please add $diffAmount " +
                        "ounces of $name to the pool and wait $hoursNotSafe before swimming."
            }
            else -> { //by process of elimination, the chemConcentration must be greater than recommended
                val diffProportion = chemConcentration - recommended
                val diffAmount = diffProportion * pool.poolGallonSize
                return "There is too much $name in the pool. Filter out $diffAmount " +
                        "ounces or add water until there are $recommended ounces of $name per gallon of water."
            }
        }

    }

}