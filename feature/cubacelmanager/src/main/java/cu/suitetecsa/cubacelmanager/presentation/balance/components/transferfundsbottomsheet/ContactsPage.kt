package cu.suitetecsa.cubacelmanager.presentation.balance.components.transferfundsbottomsheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cu.suitetecsa.cubacelmanager.presentation.balance.components.ContactView
import cu.suitetecsa.sdk.android.model.Contact

@Composable
internal fun ContactsPage(
    contacts: List<Contact>,
    onContactClick: (Contact) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(contacts) {
            ContactView(contact = it, onClick = onContactClick)
        }
    }
}
