package vn.edu.hust.studentman

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

  private val students = mutableListOf(
    StudentModel("Nguyễn Văn An", "SV001"),
    StudentModel("Trần Thị Bảo", "SV002"),
    StudentModel("Lê Hoàng Cường", "SV003"),
    StudentModel("Phạm Thị Dung", "SV004"),
    StudentModel("Đỗ Minh Đức", "SV005"),
    StudentModel("Vũ Thị Hoa", "SV006"),
    StudentModel("Hoàng Văn Hải", "SV007"),
    StudentModel("Bùi Thị Hạnh", "SV008"),
    StudentModel("Đinh Văn Hùng", "SV009"),
    StudentModel("Nguyễn Thị Linh", "SV010"),
    StudentModel("Phạm Văn Long", "SV011"),
    StudentModel("Trần Thị Mai", "SV012"),
    StudentModel("Lê Thị Ngọc", "SV013"),
    StudentModel("Vũ Văn Nam", "SV014"),
    StudentModel("Hoàng Thị Phương", "SV015"),
    StudentModel("Đỗ Văn Quân", "SV016"),
    StudentModel("Nguyễn Thị Thu", "SV017"),
    StudentModel("Trần Văn Tài", "SV018"),
    StudentModel("Phạm Thị Tuyết", "SV019"),
    StudentModel("Lê Văn Vũ", "SV020")
  )
  private lateinit var studentAdapter: StudentAdapter
  private var recentlyDeletedStudent: StudentModel? = null
  private var recentlyDeletedStudentPosition: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    studentAdapter = StudentAdapter(students, { student ->
      // Khi nhấn nút Edit, gọi hàm showEditStudentDialog
      showEditStudentDialog(student)
    }, { student ->
      // Khi nhấn nút Remove, gọi hàm showRemoveStudentDialog
      showRemoveStudentDialog(student)
    })

    findViewById<RecyclerView>(R.id.recycler_view_students).run {
      adapter = studentAdapter
      layoutManager = LinearLayoutManager(this@MainActivity)
    }

    findViewById<Button>(R.id.btn_add_new).setOnClickListener {
      showAddStudentDialog()
    }
  }

  private fun showAddStudentDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)

    val studentNameInput = dialogView.findViewById<EditText>(R.id.edit_student_name)
    val studentIdInput = dialogView.findViewById<EditText>(R.id.edit_student_id)

    val dialog = AlertDialog.Builder(this)
      .setView(dialogView)
      .setCancelable(false)
      .create()

    dialogView.findViewById<Button>(R.id.btn_add).setOnClickListener {
      val studentName = studentNameInput.text.toString().trim()
      val studentId = studentIdInput.text.toString().trim()

      if (studentName.isNotEmpty() && studentId.isNotEmpty()) {
        students.add(StudentModel(studentName, studentId))
        studentAdapter.notifyItemInserted(students.size - 1)
        dialog.dismiss()
      } else {
        // Handle empty input (e.g., show a Toast or error message)
        studentNameInput.error = "Please enter a name"
        studentIdInput.error = "Please enter an ID"
      }
    }

    dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
      dialog.dismiss()
    }

    dialog.show()
  }

  private fun showEditStudentDialog(student: StudentModel) {
    val dialogView = layoutInflater.inflate(R.layout.dialog_edit_student, null)

    val studentNameInput = dialogView.findViewById<EditText>(R.id.edit_student_name)
    val studentIdInput = dialogView.findViewById<EditText>(R.id.edit_student_id)

    studentNameInput.setText(student.studentName)
    studentIdInput.setText(student.studentId)

    val dialog = AlertDialog.Builder(this)
      .setView(dialogView)
      .setCancelable(false)
      .create()

    dialogView.findViewById<Button>(R.id.btn_save).setOnClickListener {
      val updatedStudentName = studentNameInput.text.toString().trim()
      val updatedStudentId = studentIdInput.text.toString().trim()

      if (updatedStudentName.isNotEmpty() && updatedStudentId.isNotEmpty()) {
        // Cập nhật thông tin sinh viên
        val index = students.indexOf(student)
        students[index] = StudentModel(updatedStudentName, updatedStudentId)
        studentAdapter.notifyItemChanged(index)
        dialog.dismiss()
      } else {
        // Xử lý lỗi khi không nhập thông tin
        studentNameInput.error = "Please enter a name"
        studentIdInput.error = "Please enter an ID"
      }
    }

    dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
      dialog.dismiss()
    }

    dialog.show()
  }

  private fun showRemoveStudentDialog(student: StudentModel) {
    val dialog = AlertDialog.Builder(this)
      .setMessage("Are you sure you want to delete this student?")
      .setPositiveButton("Yes") { _, _ ->
        // Save the student to restore if user chooses Undo
        recentlyDeletedStudent = student
        recentlyDeletedStudentPosition = students.indexOf(student)

        // Remove the student
        students.remove(student)
        studentAdapter.notifyDataSetChanged()

        // Show Snackbar with Undo option
        showUndoSnackbar()
      }
      .setNegativeButton("No", null)
      .create()

    dialog.show()
  }

  private fun showUndoSnackbar() {
    val snackbar = Snackbar.make(
      findViewById(android.R.id.content),
      "Student deleted",
      Snackbar.LENGTH_LONG
    )
    snackbar.setAction("Undo") {
      // Undo the deletion by adding the student back
      if (recentlyDeletedStudent != null && recentlyDeletedStudentPosition != -1) {
        students.add(recentlyDeletedStudentPosition, recentlyDeletedStudent!!)
        studentAdapter.notifyItemInserted(recentlyDeletedStudentPosition)
        recentlyDeletedStudent = null
        recentlyDeletedStudentPosition = -1
      }
    }
    snackbar.show()
  }
}