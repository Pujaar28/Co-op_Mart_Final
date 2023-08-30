package com.pujaad.coopmart.view.main_menu

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pujaad.coopmart.R
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.databinding.ActivityMainMenuBinding
import com.pujaad.coopmart.utils.SessionLogin
import com.pujaad.coopmart.view.dialog.MainFabCallback

class MainMenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainMenuBinding
    lateinit var session: SessionLogin
    lateinit var prefs: Prefs
    var fabListener: MainFabCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionLogin(this)
        prefs = Prefs(this)

        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainMenu.toolbar)

        binding.appBarMainMenu.fabAddTransaction.setOnClickListener { view ->
            fabListener?.onButtonClicked()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val header = navView.getHeaderView(0)


        // Set user credential in drawer header
        val tvUserName = header.findViewById<TextView>(R.id.tv_user_name)
        val tvUserDetail = header.findViewById<TextView>(R.id.tv_user_detail)
        val pref = Prefs(this)
        tvUserName.text = pref.user.toString()
        val userType = pref.type?.toInt() ?: 0
        tvUserDetail.text = if (userType == 0) "Admin" else "Kasir"

        val navController = findNavController(R.id.nav_host_fragment_content_main_menu)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = if (userType == 0)
            AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_users,
                    R.id.nav_pembelian,
                    R.id.nav_penjualan,
                    R.id.nav_inventory,
                    R.id.nav_inventory_category,
                    R.id.nav_supplier,
                    R.id.nav_profile,
                    R.id.nav_report,
                    R.id.nav_logout
                ), drawerLayout
            ) else {
            navView.menu.removeItem(R.id.nav_pembelian)
            navView.menu.removeItem(R.id.nav_inventory_category)
            navView.menu.removeItem(R.id.nav_supplier)
            navView.menu.removeItem(R.id.nav_report)
            navView.menu.removeItem(R.id.nav_users)

            AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_penjualan,
//                    R.id.nav_inventory_category,
                    R.id.nav_inventory,
                    R.id.nav_profile,
                    R.id.nav_logout
                ), drawerLayout
            )
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.nav_home -> binding.appBarMainMenu.fabAddTransaction.visibility = View.VISIBLE
                R.id.nav_logout -> {
                    val builder = AlertDialog.Builder(this@MainMenuActivity)
                    builder.setMessage("Yakin Anda ingin Logout?")
                    builder.setCancelable(true)
                    builder.setNegativeButton("Batal") { dialog, which -> dialog.cancel() }
                    builder.setPositiveButton("Ya") { dialog, which ->
                        logout()
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                else -> binding.appBarMainMenu.fabAddTransaction.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun logout() {
        session.logoutUser()
        prefs.clear()
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}