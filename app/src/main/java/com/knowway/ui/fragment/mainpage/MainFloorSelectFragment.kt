
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.knowway.R
import com.knowway.adapter.FloorAdapter
import com.knowway.data.model.department.Floor
import com.knowway.databinding.FragmentFloorSelectBinding
import com.knowway.ui.activity.mainpage.MainPageActivity

class MainFloorSelectFragment : DialogFragment() {
    private var _binding: FragmentFloorSelectBinding? = null
    private val binding get() = _binding!!
    private val adapter: FloorAdapter by lazy {
        FloorAdapter { floor ->
            val sharedPreferences = requireContext().getSharedPreferences("FloorPref", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putLong("selected_floor_id", floor.departmentStoreFloorId)
                putString("selected_floor_name", floor.departmentStoreFloor)
                putString("selected_floor_map_path", floor.departmentStoreMapPath)
                apply()
            }
            (activity as? MainPageActivity)?.updateCurrentFloor(floor)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFloorSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floorRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.floorRecyclerView.adapter = adapter

        loadAndDisplayFloorData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun loadAndDisplayFloorData() {
        val sharedPreferences = requireContext().getSharedPreferences("DeptPref", MODE_PRIVATE)
        val floorIds = sharedPreferences.getString("dept_floor_ids", "")
        val floorNames = sharedPreferences.getString("dept_floor_names", "")
        val floorMapPaths = sharedPreferences.getString("dept_floor_map_paths", "")

        val floorIdList = floorIds?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val floorNameList = floorNames?.split(",") ?: emptyList()
        val floorMapPathList = floorMapPaths?.split(",") ?: emptyList()

        val floorList = floorIdList.zip(floorNameList).zip(floorMapPathList) { (id, name), mapPath ->
            Floor(id, name, mapPath)
        }

        adapter.submitList(floorList)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawableResource(R.drawable.border_background)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes?.height = (resources.displayMetrics.heightPixels * 0.6).toInt()
        }
        return dialog
    }
}
