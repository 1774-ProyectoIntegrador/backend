package proyecto.dh.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CreateProductDTO;
import proyecto.dh.resources.product.entity.ProductCategory;
import proyecto.dh.resources.product.repository.ProductCategoryRepository;
import proyecto.dh.resources.product.service.ProductService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private Map<String, Long> categoryMap = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadProductData();
    }

    private void loadProductData() {
        // Crear categorías de productos
        ProductCategory categoryCameras = new ProductCategory();
        categoryCameras.setName("Cámaras");
        categoryCameras.setSlug("cameras");

        ProductCategory categoryLenses = new ProductCategory();
        categoryLenses.setName("Lentes");
        categoryLenses.setSlug("lenses");

        ProductCategory categoryLights = new ProductCategory();
        categoryLights.setName("Luces");
        categoryLights.setSlug("lights");

        ProductCategory categoryAudio = new ProductCategory();
        categoryAudio.setName("Audio");
        categoryAudio.setSlug("audio");

        ProductCategory categoryProfessionals = new ProductCategory();
        categoryProfessionals.setName("Profesionales");
        categoryProfessionals.setSlug("professionals");

        // Guardar categorías
        productCategoryRepository.saveAll(Arrays.asList(categoryCameras, categoryLenses, categoryLights, categoryAudio, categoryProfessionals));

        // Guardar las categorías en el mapa para fácil acceso por nombre
        categoryMap.put("Cámaras", categoryCameras.getId());
        categoryMap.put("Lentes", categoryLenses.getId());
        categoryMap.put("Luces", categoryLights.getId());
        categoryMap.put("Audio", categoryAudio.getId());
        categoryMap.put("Profesionales", categoryProfessionals.getId());

        // Crear productos de prueba
        try {
            productService.save(new CreateProductDTO("Cámara Canon C200, Cine Digital, 3 zoom Canon", "La FX3 es una cámara compacta pensada primero para producciones de video.", 5, 230000.0, RentType.DAILY, categoryMap.get("Cámaras")));
            productService.save(new CreateProductDTO("Cámara Sony A7 III", "Cámara de fotograma completo con capacidades de video 4K.", 3, 200000.0, RentType.WEEKLY, categoryMap.get("Cámaras")));
            productService.save(new CreateProductDTO("Cámara Blackmagic Pocket 6K", "Cámara de cine digital compacta con resolución 6K.", 4, 250000.0, RentType.MONTHLY, categoryMap.get("Cámaras")));
            productService.save(new CreateProductDTO("Lente Canon EF 24-70mm f/2.8L II USM", "Lente estándar de alta calidad con una apertura rápida y constante.", 6, 50000.0, RentType.YEARLY, categoryMap.get("Lentes")));
            productService.save(new CreateProductDTO("Lente Sigma 18-35mm f/1.8 DC HSM Art", "Lente de zoom rápido y versátil con excelente calidad de imagen.", 7, 60000.0, RentType.DAILY, categoryMap.get("Lentes")));
            productService.save(new CreateProductDTO("Lente Tamron 70-200mm f/2.8 Di VC USD G2", "Lente teleobjetivo con estabilización de imagen y rápido enfoque.", 5, 70000.0, RentType.WEEKLY, categoryMap.get("Lentes")));
            productService.save(new CreateProductDTO("Luz LED Neewer 660 RGB", "Luz LED RGB con alto rendimiento y ajuste de brillo.", 8, 30000.0, RentType.MONTHLY, categoryMap.get("Luces")));
            productService.save(new CreateProductDTO("Luz Aputure Light Storm C300d II", "Luz LED de alta potencia y calidad para producciones cinematográficas.", 3, 100000.0, RentType.YEARLY, categoryMap.get("Luces")));
            productService.save(new CreateProductDTO("Luz Godox SL60W", "Luz LED asequible y eficiente para fotografía y video.", 9, 25000.0, RentType.DAILY, categoryMap.get("Luces")));
            productService.save(new CreateProductDTO("Micrófono Rode NTG5", "Micrófono shotgun ligero y compacto para grabación profesional.", 5, 40000.0, RentType.WEEKLY, categoryMap.get("Audio")));
            productService.save(new CreateProductDTO("Micrófono Sennheiser MKH 416", "Micrófono shotgun profesional ampliamente utilizado en la industria.", 4, 45000.0, RentType.MONTHLY, categoryMap.get("Audio")));
            productService.save(new CreateProductDTO("Micrófono Shure SM7B", "Micrófono dinámico de estudio conocido por su calidad de sonido.", 6, 35000.0, RentType.YEARLY, categoryMap.get("Audio")));
            productService.save(new CreateProductDTO("Servicio de Fotógrafo Profesional", "Fotógrafo profesional con experiencia en eventos y sesiones fotográficas.", 2, 150000.0, RentType.DAILY, categoryMap.get("Profesionales")));
            productService.save(new CreateProductDTO("Servicio de Sonidista Profesional", "Sonidista con equipo completo para grabaciones de alta calidad.", 2, 130000.0, RentType.WEEKLY, categoryMap.get("Profesionales")));
            productService.save(new CreateProductDTO("Servicio de Camarógrafo Profesional", "Camarógrafo con experiencia en cine y televisión, disponible para proyectos.", 2, 180000.0, RentType.MONTHLY, categoryMap.get("Profesionales")));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
