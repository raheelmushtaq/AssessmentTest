package com.app.assesmenttest.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.wear.compose.material.CircularProgressIndicator
import com.app.assesmenttest.R
import com.app.assesmenttest.room.entity.Medicine
import com.app.assesmenttest.ui.components.appheader.AppHeader
import com.app.assesmenttest.ui.components.textfields.LargeText
import com.app.assesmenttest.ui.components.textfields.MediumText
import com.app.assesmenttest.ui.components.textfields.RegularText
import com.app.assesmenttest.ui.components.textfields.SmallText
import com.app.assesmenttest.ui.screens.destinations.MedicineDetailScreenDestination
import com.app.assesmenttest.ui.screens.home.component.MedicineListItem
import com.app.assesmenttest.ui.screens.home.viewmodel.HomeViewModel
import com.app.assesmenttest.utils.addDelay
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination()
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator, email: String, viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val listOfMedicine = viewModel.medicineFlow.collectAsLazyPagingItems()
    var isAppScreenLoading = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = true) {
        addDelay(1000) {
            isAppScreenLoading.value = !isAppScreenLoading.value
        }
    }

    LaunchedEffect(key1 = listOfMedicine.loadState) {
        if (listOfMedicine.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                (listOfMedicine.loadState.refresh as LoadState.Error).error.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {

        AppHeader(navHostController = navigator, title = email)
        HomeScreenComponent(listOfMedicine, isAppScreenLoading.value) { item ->
            navigator.navigate(MedicineDetailScreenDestination(item))

        }
    }
}

@Composable
fun HomeScreenComponent(
    listOfMedicine: LazyPagingItems<Medicine>,
    isAppLoading: Boolean,
    onItemClick: (Medicine) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(10.dp)

    ) {

        if (listOfMedicine.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                indicatorColor = Color.Black, modifier = Modifier.align(
                    Alignment.Center
                )
            )
        } else {
            if (listOfMedicine.itemCount == 0 && !isAppLoading) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LargeText(
                            text = stringResource(id = R.string.no_record_found),
                            fontWeight = FontWeight.Bold
                        )
                        MediumText(
                            text = stringResource(id = R.string.no_record_found_message),
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(bottom = 30.dp),
                ) {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(listOfMedicine.itemCount) { index ->
                            val item = listOfMedicine[index]
                            if (item != null) MedicineListItem(item = item) {
                                onItemClick(item)
                            }
                        }

                        item {
                            if (listOfMedicine.loadState.append is LoadState.Loading) {
                                CircularProgressIndicator(indicatorColor = Color.Black)
                            }
                        }
                    }
                }
            }

        }

    }

}