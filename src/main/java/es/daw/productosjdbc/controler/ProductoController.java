package es.daw.productosjdbc.controler;

import org.springframework.ui.Model;
import es.daw.productosjdbc.dto.ProductoForm;
import es.daw.productosjdbc.model.Producto;
import es.daw.productosjdbc.service.FabricanteService;
import es.daw.productosjdbc.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    // El controlador solo conoce SERVICIOS, nunca repositorios
    private final ProductoService productoService;
    private final FabricanteService fabricanteService;

    public ProductoController(ProductoService productoService,
                              FabricanteService fabricanteService) {
        this.productoService = productoService;
        this.fabricanteService = fabricanteService;
    }

    // READ: listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/lista";
    }

    // Formulario vacío para alta
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("productoForm", new ProductoForm());
        model.addAttribute("fabricantes", fabricanteService.listar());
        return "productos/formulario";
    }

    // Formulario relleno para edición
    @GetMapping("/editar/{codigo}")
    public String editar(@PathVariable Integer codigo, Model model) {
        Producto producto = productoService.buscarPorCodigo(codigo);   // 404 si no existe

        model.addAttribute("productoForm", ProductoForm.desde(producto));
        model.addAttribute("fabricantes", fabricanteService.listar());
        return "productos/formulario";
    }

    // CREATE / UPDATE: el mismo formulario sirve para las dos operaciones
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute ProductoForm productoForm,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Volvemos al formulario: hay que recargar el desplegable
            model.addAttribute("fabricantes", fabricanteService.listar());
            return "productos/formulario";
        }

        if (productoForm.getCodigo() == null) {
            Producto creado = productoService.crear(productoForm.aProducto());
            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto creado con código " + creado.getCodigo());
        } else {
            productoService.actualizar(productoForm.aProducto());
            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto " + productoForm.getCodigo() + " actualizado");
        }

        // PRG (Post/Redirect/Get): si el usuario pulsa F5 no se reenvía el formulario
        return "redirect:/productos";
    }
    // DELETE: por POST, nunca por GET (un GET no debe modificar datos)
    @PostMapping("/borrar/{codigo}")
    public String borrar(@PathVariable Integer codigo,
                         RedirectAttributes redirectAttributes) {
        productoService.borrar(codigo);
        redirectAttributes.addFlashAttribute("mensaje", "Producto " + codigo + " borrado");
        return "redirect:/productos";
    }
}