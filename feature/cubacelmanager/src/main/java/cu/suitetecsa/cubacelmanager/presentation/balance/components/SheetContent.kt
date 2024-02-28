package cu.suitetecsa.cubacelmanager.presentation.balance.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cu.suitetecsa.sdk.android.model.Contact

@Composable
fun ContactView(
    contact: Contact,
    onClick: (Contact) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(contact) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactImage(photoUriString = contact.photoUri)
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = contact.phoneNumber,
                fontWeight = FontWeight.Light
            )
        }
    }
}
