package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.adretsoftware.mehndipvcinterior.models.InvoiceData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class InvoiceGenerator(activity: AppCompatActivity,data: InvoiceData) {
    lateinit var orderId:String
    lateinit var invoiceId:String
    lateinit var activity:AppCompatActivity
    lateinit var invoiceUri: Uri
    init {
        this.activity=activity
        val officeAddress ="MEHNDI PROFILE INDUSTRIES PVT. LTD.\nSiv Sai Puram, Badangpet, hyderabad, Telangana, 500058\nPhone no.: 9246272658\nEmail: info@mehndipvc.com\nGSTIN: 19AAQCM4236P1ZM\nState: 19-West Bengal"

        val pdfpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        var file = File(pdfpath, "invoice.pdf")
        invoiceUri=file.toUri()
        var outputStream = FileOutputStream(file)
        var pdfWriter = PdfWriter(outputStream)
        var pdfDocument = PdfDocument(pdfWriter)
        pdfDocument.defaultPageSize = PageSize.A4
        var document = Document(pdfDocument)


        document.setFontSize(6f)
        val headingTable = Table(floatArrayOf(300f, 300f))

        headingTable.addCell(
            Cell().setBorder(Border.NO_BORDER)
                .add(Paragraph(officeAddress).setTextAlignment(TextAlignment.LEFT)).setPadding(20f)
        )


        val drawable=getDrawable(activity,R.drawable.app_logo)
        val bitmap= (drawable as BitmapDrawable).bitmap
        val stream=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        val bitmapData=stream.toByteArray()
        var imageData= ImageDataFactory.create(bitmapData)
        val image= Image(imageData)
        image.setHeight(35f);image.setWidth(80f)
        headingTable.addCell(Cell().setBorder(Border.NO_BORDER).add(image.setHorizontalAlignment(HorizontalAlignment.RIGHT)))


        document.add(headingTable)
        document.add(Paragraph("Tax Invoice").setFontSize(15F).setPaddingLeft(220f))

        val userInfoTable = Table(floatArrayOf(300f, 300f)).setHorizontalAlignment(HorizontalAlignment.CENTER)
        userInfoTable.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Bill to:\n${data.user.name}\nContact no.: ${data.user.mobile}"))
            .setPadding(30f).setBold()
        )
        userInfoTable.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Order number: ${data.order.order_id}\nDate.: ${data.order.date}"))
                .setPadding(30f).setBold()
        )

        document.add(userInfoTable)

        document.add(
            Paragraph("______________________________________________")
                .setFontColor(DeviceRgb(13, 131, 221)).setFontSize(20F)
        )

        val itemHeading = Table(floatArrayOf(80f,150f,80f,80f,80f,80f)).setBackgroundColor(DeviceRgb(76, 30, 13))
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("#"))
                .setPadding(2f)
        )
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Item name"))
                .setPadding(2f).setBackgroundColor(DeviceRgb(76, 30, 13)).setFontColor(DeviceRgb(255,255,255))
        )
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("model number"))
                .setPadding(2f).setBackgroundColor(DeviceRgb(76, 30, 13)).setFontColor(DeviceRgb(255,255,255))
        )
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Quantity"))
                .setPadding(2f).setBackgroundColor(DeviceRgb(76, 30, 13)).setFontColor(DeviceRgb(255,255,255))
        )
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Price/unit"))
                .setPadding(2f).setBackgroundColor(DeviceRgb(76, 30, 13)).setFontColor(DeviceRgb(255,255,255))
        )
        itemHeading.addCell(
            Cell().setBorder(Border.NO_BORDER).add(Paragraph("Amount"))
                .setPadding(2f).setBackgroundColor(DeviceRgb(76, 30, 13)).setFontColor(DeviceRgb(255,255,255))
        )
        document.add(itemHeading)


        var i=1
        var totalQuant=0
        for (a in data.items) {
            val itemTable =Table(floatArrayOf(80f,150f,80f,80f,80f,80f))

            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph("$i"))
                    .setPadding(2f)
            )
            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph(a.name))
                    .setPadding(2f)
            )
            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph(a.code))
                    .setPadding(2f)
            )
            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph(a.quantity))
                    .setPadding(2f)
            )
            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph("Rs "+a.price))
                    .setPadding(2f)
            )
            itemTable.addCell(
                Cell().setBorder(Border.NO_BORDER).add(Paragraph(a.total_price))
                    .setPadding(2f)
            )
            i++
            totalQuant+=a.quantity.toInt()
            document.add(itemTable)
        }

        document.add(
            Paragraph("______________________________________________")
                .setFontColor(DeviceRgb(221,221,221)).setFontSize(20F)
        )


        val finalPriceTable = Table(floatArrayOf(300f, 200f,200f)).setBold()
        finalPriceTable.addCell(
            Cell().setBorder(Border.NO_BORDER).setBold()
                .add(Paragraph("Total "))
        )
        finalPriceTable.addCell(
            Cell().setBorder(Border.NO_BORDER).setBold()
                .add(Paragraph(totalQuant.toString()))
        )
        finalPriceTable.addCell(
            Cell().setBorder(Border.NO_BORDER).setBold()
                .add(Paragraph("Rs ${data.order.price}"))
        )
        document.add(finalPriceTable)

        document.add(
            Paragraph("______________________________________________")
                .setFontColor(DeviceRgb(221,221,221)).setFontSize(20F)
        )

        val bottomTable = Table(floatArrayOf(300f, 300f))

        val bottomFirstPortion=Table(floatArrayOf(300f)).setFontColor(DeviceRgb(116,116,116))
        bottomFirstPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("INVOICE AMOUNT IN WORDS")))
        bottomFirstPortion.addCell(Cell().setBackgroundColor(DeviceRgb(247,247,247)) .setBorder(Border.NO_BORDER).add(Paragraph("Five Lakh Seventy Four Thousand Four Hundred and Eighty Rupees only")))
        bottomFirstPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("TERMS AND CONDITIONS")))
        bottomFirstPortion.addCell(Cell().setBackgroundColor(DeviceRgb(247,247,247)) .setBorder(Border.NO_BORDER).add(Paragraph(
            "*SERVICES & WARRANTY : -\n" +
                "PVC Materials Lifetime Warranty (Self Breakages Not Allowed)\n" +
                "Free Services: - (No. 2) In 1st Years.\n" +
                "For Future Maintenance: - Please Contact us For AMC after 1 Years.\n" +
                "*TRANSACTIONS\n" +
                "Make financial transactions in the \"Mehndi Interior\" or \"Mehendi\n" +
                "Profile Industries Pvt. Ltd.\" Bank account, in case of cash transactions\n" +
                "you must check the receipt copy of the company's Hallmark.\n" +
                "SERVICE & SUPPORT CALL TOLL FREE : - 18008911988\n" +
                "* Please Give us Your Valuable feedback & Review on Google &\n" +
                    "Justdial. Thank you for doing business with us.\n")))
        val bottomSecondPortion=Table(floatArrayOf(100f,150f)).setBorder(Border.NO_BORDER)
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Sub Total")))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Rs "+data.order.price)))
        bottomSecondPortion.addCell(Cell().setBackgroundColor(DeviceRgb(76,30,13)).setFontColor(DeviceRgb(255,255,255)) .setBorder(Border.NO_BORDER).add(Paragraph("Total")))
        bottomSecondPortion.addCell(Cell().setBackgroundColor(DeviceRgb(76,30,13)).setFontColor(DeviceRgb(255,255,255)).setBorder(Border.NO_BORDER).add(Paragraph("Rs "+data.order.price)))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Received")))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Rs 0")))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Balance")))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Rs "+data.order.price)))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Payment Mode")))
        bottomSecondPortion.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Credit")))
        bottomTable.addCell(bottomFirstPortion)
        bottomTable.addCell(bottomSecondPortion)
        document.add(bottomTable)



        val lastTable = Table(floatArrayOf(100f,250f, 250f))

        val drawable2=getDrawable(activity,R.drawable.qrcode)
        val bitmap2= (drawable2 as BitmapDrawable).bitmap
        val stream2=ByteArrayOutputStream()
        bitmap2.compress(Bitmap.CompressFormat.PNG,100,stream2)
        val bitmapData2=stream2.toByteArray()
        var imageData2= ImageDataFactory.create(bitmapData2)
        val image2= Image(imageData2)
        image2.setHeight(100f);image2.setWidth(80f)

        lastTable.addCell(Cell().setBorder(Border.NO_BORDER).add(image2))

        lastTable.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("Pay ToBank Name: AXIS BANK, RAJPUR\n" +
                "Bank Account No.:\n" +
                "922020012624167\n\n" +
                "Bank IFSC code: UTIB0001481\n\n" +
                "Account Holder's Name: MEHNDI\n" +
                "INTERIOR")))
        lastTable.addCell(Cell().setBorder(Border.NO_BORDER).add(Paragraph("For, MEHNDI PROFILE INDUSTRIES PVT. LTD.\n\n\n\n\n" +
                "Authorized Signatory\n")))

        document.add(lastTable)

        document.close()





//        val pdfFile = File(pdfpath, "namePdfFile.pdf") //File path
        if (file.exists()) //Checking if the file exists or not
        {
//            val path = Uri.fromFile(file)
            val photoURI: Uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file)
            val objIntent = Intent(Intent.ACTION_VIEW)
            objIntent.setDataAndType(photoURI, "application/pdf")
            objIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(activity,objIntent,null) //Starting the pdf viewer
        } else {
            Toast.makeText(activity, "The file not exists! ", Toast.LENGTH_SHORT).show()
        }
    }
}