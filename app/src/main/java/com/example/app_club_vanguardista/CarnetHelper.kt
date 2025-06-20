import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.app_club_vanguardista.R // Asegúrate que R esté bien importado

// Data class para pasar la info del socio
data class DatosSocioParaCarnet(
    val nombreCompleto: String,
    val dni: String,
    val tipoSocio: String,
    val fechaVencimiento: String,
    val fotoBytes: ByteArray?, // Para la foto del socio
    val idParaCodigoBarras: String? = null // Para el código de barras/QR
)

fun generarBitmapCarnet(context: Context, datosSocio: DatosSocioParaCarnet): Bitmap? {
    val inflater = LayoutInflater.from(context)
    // Usa el ID del layout raíz que definiste en carnet_club_layout.xml
    val carnetView = inflater.inflate(R.layout.activity_carnet, null, false)

    // Referencias a las vistas dentro de carnet_club_layout.xml
    val imgLogo: ImageView = carnetView.findViewById(R.id.imgLogoClubCarnet)
    val tvNombreClub: TextView = carnetView.findViewById(R.id.tvNombreClubCarnet)
    val imgFotoSocio: ImageView = carnetView.findViewById(R.id.imgFotoSocioEnCarnet)
    val tvNombreSocio: TextView = carnetView.findViewById(R.id.tvNombreSocioEnCarnet)
    val tvDniSocio: TextView = carnetView.findViewById(R.id.tvDniSocioEnCarnet)
    val tvTipoSocio: TextView = carnetView.findViewById(R.id.tvTipoSocioEnCarnet)
    val tvVencimiento: TextView = carnetView.findViewById(R.id.tvVencimientoSocioEnCarnet)

    // Poblar los datos
    // imgLogo.setImageResource(R.drawable.tu_logo_real_aqui) // Si es estático o desde drawable
    tvNombreSocio.text = "Nombre: ${datosSocio.nombreCompleto}"
    tvDniSocio.text = "DNI: ${datosSocio.dni}"
    tvTipoSocio.text = "Tipo: ${datosSocio.tipoSocio}"
    tvVencimiento.text = "Vence: ${datosSocio.fechaVencimiento}"

    if (datosSocio.fotoBytes != null && datosSocio.fotoBytes.isNotEmpty()) {
        val bitmapFoto = BitmapFactory.decodeByteArray(datosSocio.fotoBytes, 0, datosSocio.fotoBytes.size)
        imgFotoSocio.setImageBitmap(bitmapFoto)
    } else {
        imgFotoSocio.setImageResource(R.drawable.avatar) // Imagen por defecto
    }

    // Si generas un código de barras/QR, necesitarías una función para ello
    // y luego:
    // datosSocio.idParaCodigoBarras?.let { id ->
    //     val bitmapCodigo = generarCodigoBarrasBitmap(id, 200, 50) // Ejemplo
    //     imgCodigoExtra.setImageBitmap(bitmapCodigo)
    // }


    // Medir y dibujar la vista en un Bitmap
    // Usar las dimensiones definidas en el XML (ej. 350dp x 200dp)
    val widthPx = dpToPx(context, 350)
    val heightPx = dpToPx(context, 200)

    carnetView.measure(
        View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
    )
    carnetView.layout(0, 0, carnetView.measuredWidth, carnetView.measuredHeight)

    val bitmap = Bitmap.createBitmap(carnetView.measuredWidth, carnetView.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    carnetView.draw(canvas)

    return bitmap
}

// Helper para convertir dp a px
fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
fun generarPdfDesdeBitmap(context: Context, bitmapCarnet: Bitmap, nombreArchivo: String): File? {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmapCarnet.width, bitmapCarnet.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    page.canvas.drawBitmap(bitmapCarnet, 0f, 0f, null)
    pdfDocument.finishPage(page)

    // Determinar la ruta de guardado
    val directorioPdf: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    if (directorioPdf != null && !directorioPdf.exists()) {
        directorioPdf.mkdirs()
    }

    val archivoPdf = File(directorioPdf, nombreArchivo)

    try {
        FileOutputStream(archivoPdf).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
        return archivoPdf
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close() // Asegúrate de cerrar incluso si hay error
        return null
    }
}