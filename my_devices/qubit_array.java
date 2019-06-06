import com.comsol.model.*;
import com.comsol.model.util.*;
import comsolbuilder.*;
import java.util.*;

public class qubit_array {

   public static void main(String[] args) {
      run();
   }
   public static Model run() {
      // First, define materials used in study
      ComsolMaterial si = new ComsolMaterial("Si",11.7);
      ComsolMaterial sige = new ComsolMaterial("SiGe",13.19);
      ComsolMaterial alO2 = new ComsolMaterial("AlO2",9.);
      ComsolMaterial al = new ComsolMaterial("Al",1.);
      // the x-y span of the geometry is 1000 nm x 1000 nm
      Double xDims = 1000.;
      Double yDims = 1000.;
      // Create ComsolBuilder instance
      ComsolBuilder builder = new ComsolBuilder(xDims,yDims);
      // Define heterostructure layer names and heights, add them to the builder
      String layerLabels[] = {"SiGeBuffer","SiWell","SiGeSpacer","Oxide"};
      String layerHeights[] = {"170.","9.","40.","240."};
      builder.addHeterostructure(layerLabels,layerHeights);
      // Add STL files to builder
      String stlFolder = "/Users/MPRL/Developer/comsol-builder/my_devices/stl_files";//TODO: make this a relative path?
      builder.addElectrodesSTL(stlFolder,224.);//TODO: change 224 to variable
      // Finish building geometry
      builder.model.geom("geom1").run("fin");
      //Specify which materials are used with which domains
      builder.addMaterial(si,"geom1_SiWell_dom");//TODO: user shouldn't have to know domain names
      builder.addMaterial(sige,"geom1_SiGeSel_dom");
      builder.addMaterial(alO2,"geom1_Oxide_dom");
      builder.addMaterial(al,"geom1_ElectrodeSel_dom");
      //Add electrostatics to model
      builder.model.physics().create("es", "Electrostatics", "geom1");
      //Each domain in domain group is a different electrode
      builder.selectVoltages("geom1_ElectrodeSel_dom");

      // Adds an electrostatic element for the surface charge in the SiWell
      builder.addSurfaceChargeDensity();

      Dictionary params = new Hashtable();

      // Electrode voltages
      params.put("screening", "0[V]");
      params.put("qubit_control", ".02[V]");
      params.put("readout_tunnel", "0[V]");
      params.put("qubit_plunger", ".02[V]");
      params.put("tunnel_barrier", "-.004[V]");
      params.put("readout_plunger", ".02[V]");

      // Constants
      params.put("valley_degeneracy","2");
      params.put("effective_mass", "0.19");

      // Dot location and size params
      params.put("dot_1a_a_semiaxis", "40[nm]");
      params.put("dot_1a_b_semiaxis", "55[nm]");
      params.put("dot_1a_x_center", "-220[nm]");
      params.put("dot_1a_y_center", "80[nm]");
      params.put("dot_1b_y_center", "-40[nm]");
      params.put("dot_1b_y_center", "-40[nm]");
      params.put("dot_2a_x_center", "115[nm]");
      params.put("dot_5a_y_center", "120[nm]");

      for (Enumeration i = params.keys(); i.hasMoreElements();) 
      { 
         String key = i.nextElement().toString();
         builder.addParameter(key, params.get(key).toString());
      } 

      builder.addEllipseInSiWell("el1", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "dot_1a_x_center", "dot_1a_y_center");
      builder.addEllipseInSiWell("el2", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "dot_1a_x_center", "dot_1b_y_center");
      builder.addEllipseInSiWell("el3", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "dot_2a_x_center", "dot_1a_y_center");
      builder.addEllipseInSiWell("el4", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "dot_2a_x_center", "dot_1b_y_center");
      builder.addEllipseInSiWell("el5", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "-dot_2a_x_center", "dot_1a_y_center");
      builder.addEllipseInSiWell("el6", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "-dot_2a_x_center", "dot_1b_y_center");
      builder.addEllipseInSiWell("el7", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "-dot_1a_x_center", "dot_1a_y_center");
      builder.addEllipseInSiWell("el8", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "-dot_1a_x_center", "dot_1b_y_center");
      builder.addEllipseInSiWell("el9", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "0", "dot_5a_y_center");
      builder.addEllipseInSiWell("el10", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "0", "0");
      builder.addEllipseInSiWell("el11", "dot_1a_a_semiaxis", "dot_1a_b_semiaxis", "0", "-dot_5a_y_center");



      return builder.model;
   }

}
