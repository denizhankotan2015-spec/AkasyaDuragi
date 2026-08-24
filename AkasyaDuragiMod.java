package com.akasyaduragi;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(AkasyaDuragiMod.MODID)
public class AkasyaDuragiMod {
    public static final String MODID = "akasyaduragi";
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // =========================================================================
    // SIFIRDAN BAĞIMSIZ DOĞAN GERÇEK AKASYA DURAĞI KARAKTER KAYITLARI (26.2 FORGE)
    // =========================================================================
    public static final RegistryObject<EntityType<OsmanAgaEntity>> OSMAN_AGA = ENTITIES.register("osman_aga", () -> EntityType.Builder.of(OsmanAgaEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("osman_aga"));
    public static final RegistryObject<EntityType<SinanEntity>> SINAN = ENTITIES.register("sinan", () -> EntityType.Builder.of(SinanEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("sinan"));
    public static final RegistryObject<EntityType<NuriBabaEntity>> NURI_BABA = ENTITIES.register("nuri_baba", () -> EntityType.Builder.of(NuriBabaEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("nuri_baba"));
    public static final RegistryObject<EntityType<AliKemalEntity>> ALI_KEMAL = ENTITIES.register("ali_kemal", () -> EntityType.Builder.of(AliKemalEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build("ali_kemal"));
    public static final RegistryObject<EntityType<SanzimentEntity>> SANZIMENT = ENTITIES.register("sanziment", () -> EntityType.Builder.of(SanzimentEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("sanziment"));
    public static final RegistryObject<EntityType<GulibikEntity>> GULIBIK = ENTITIES.register("gulibik", () -> EntityType.Builder.of(GulibikEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("gulibik"));
    public static final RegistryObject<EntityType<ArifEntity>> ARIF = ENTITIES.register("arif", () -> EntityType.Builder.of(ArifEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("arif"));
    public static final RegistryObject<EntityType<MafyaEntity>> MAFYALAR = ENTITIES.register("mafyalar", () -> EntityType.Builder.of(MafyaEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build("mafyalar"));
    
    // SAFİYE VE ZEYNO SIFIRDAN DOST CANLISI (CREATURE) OLARAK KODA DAHİL EDİLDİ!
    public static final RegistryObject<EntityType<SafiyeEntity>> SAFIYE = ENTITIES.register("safiye", () -> EntityType.Builder.of(SafiyeEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("safiye"));
    public static final RegistryObject<EntityType<ZeynoEntity>> ZEYNO = ENTITIES.register("zeyno", () -> EntityType.Builder.of(ZeynoEntity::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("zeyno"));

    public AkasyaDuragiMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    // =========================================================================
    // 1. OSMAN AGA: 500 Can, Tüfek Patlaması, Börek ve İnternetsiz Lokal Skin Kodları
    // =========================================================================
    public static class OsmanAgaEntity extends Monster {
        private boolean isSinirli = false;
        private int sinirZamani = 0;

        public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/osman_aga.png");

        protected OsmanAgaEntity(EntityType<? extends Monster> type, Level level) { 
            super(type, level);
            this.setCustomName(Component.literal("Osman Aga"));
            this.setCustomNameVisible(true);
        }

        public static AttributeSupplier.Builder createAttributes() {
            return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 40.0D);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

        @Override
        public void tick() {
            super.tick();
            if (isSinirli) {
                this.refreshDimensions();
                sinirZamani++;
                if (sinirZamani % 20 == 0 && this.getTarget() != null) {
                    Vec3 targetPos = this.getTarget().position();
                    this.level().explode(this, targetPos.x, targetPos.y, targetPos.z, 1.0F, Level.ExplosionInteraction.TNT);
                }
            }
        }

        @Override
        protected InteractionResult mobileInteract(Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.COOKED_CHICKEN) && this.isSinirli) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.isSinirli = false; this.setTarget(null);
                player.displayClientMessage(Component.literal("Osman Aga: Ah be kızanım, börek de pek lezzetliymiş be ya!"), true);
                return InteractionResult.SUCCESS;
            }
            return super.mobileInteract(player, hand);
        }

        public void nuriBabayaSaldirdilar() {
            this.isSinirli = true;
            this.sendSystemMessage(Component.literal("Osman Aga: EEM NURİ BABA'NIN CANINA KAST ETMEK NEYMİŞ GÖSTERECEM ŞİMDİ SİZE HE!"));
        }
    }

    // =========================================================================
    // 2. SAFİYE & ZEYNO: Cana Yakın, Hep Evde Durma Hedefli Karakter Yapay Zekası
    // =========================================================================
    public static class SafiyeEntity extends Animal {
        public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/safiye.png");
        protected SafiyeEntity(EntityType<? extends Animal> type, Level level) { 
            super(type, level);
            this.setCustomName(Component.literal("Safiye"));
            this.setCustomNameVisible(true);
        }
        public static AttributeSupplier.Builder createAttributes() {
            return Animal.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.20D);
        }
        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D)); // Çok uzaklaşmasın, sakin takılsın
        }
        @Override public Animal getBreedOffspring(net.minecraft.server.level.ServerLevel lvl, Animal anim) { return null; }
    }

    public static class ZeynoEntity extends Animal {
        public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/zeyno.png");
        protected ZeynoEntity(EntityType<? extends Animal> type, Level level) { 
            super(type, level);
            this.setCustomName(Component.literal("Zeyno"));
            this.setCustomNameVisible(true);
        }
        public static AttributeSupplier.Builder createAttributes() {
            return Animal.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.20D);
        }
        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        }
        @Override public Animal getBreedOffspring(net.minecraft.server.level.ServerLevel lvl, Animal anim) { return null; }
    }

    // =========================================================================
    // 3. SİNAN KAYA: 40 Can, 5 Blok Yakından En Değerli Madeni Çalıp Hız III Kaçış
Kodu dikkatli kullanın.// =========================================================================public static class SinanEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/sinan.png");protected SinanEntity(EntityType<? extends Monster> type, Level level) {super(type, level);this.setCustomName(Component.literal("Sinan Kaya"));this.setCustomNameVisible(true);}public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.32D).add(Attributes.ATTACK_DAMAGE, 6.0D);}@Overrideprotected void registerGoals() {this.goalSelector.addGoal(1, new FloatGoal(this));this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));}@Overridepublic void tick() {super.tick();Player player = this.level().getNearestPlayer(this, 5.0D);if (player != null && this.tickCount % 100 == 0 && !player.getInventory().isEmpty()) {for (int i = 0; i < player.getInventory().getContainerSize(); i++) {ItemStack stack = player.getInventory().getItem(i);if (stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || stack.is(Items.NETHERITE_INGOT)) {player.getInventory().removeItem(stack);this.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 120, 2));for(int j=0; j<15; j++) { this.level().addParticle(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 0, 0.1, 0); }break;}}}}}// =========================================================================// 4. NURİ BABA: Telsiz Bağırma Mekaniği// =========================================================================public static class NuriBabaEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/nuri_baba.png");protected NuriBabaEntity(EntityType<? extends Monster> type, Level level) {super(type, level);this.setCustomName(Component.literal("Nuri Baba"));this.setCustomNameVisible(true);}public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.22D);}@Overridepublic boolean hurt(DamageSource source, float amount) {if (source.getEntity() instanceof Player player) {this.sendSystemMessage(Component.literal("Nuri Baba: Sinaaan, Osmaaan! Yetişin canıma kast ediyorlar"));java.util.List osmanlar = this.level().getEntitiesOfClass(OsmanAgaEntity.class, this.getBoundingBox().inflate(50.0D));for (OsmanAgaEntity osman : osmanlar) { osman.nuriBabayaSaldirdilar(); osman.setTarget(player); }}return super.hurt(source, amount);}}// =========================================================================// 5. KORKAK ARİF & FİLMDEKİ MAFYALAR// =========================================================================public static class ArifEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/arif.png");protected ArifEntity(EntityType<? extends Monster> type, Level level) {super(type, level);this.setCustomName(Component.literal("Arif"));this.setCustomNameVisible(true);}public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);}@Overrideprotected void registerGoals() {this.goalSelector.addGoal(1, new FloatGoal(this));this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.4D, 1.8D));this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 10.0F, 1.4D, 1.8D));}@Overridepublic void tick() {super.tick();if (this.tickCount % 5 == 0) {this.level().addParticle(ParticleTypes.DRIPPING_WATER, this.getX(), this.getY() + 1.5D, this.getZ(), 0, 0, 0);}}}public static class MafyaEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/mafya.png");protected MafyaEntity(EntityType<? extends Monster> type, Level level) {super(type, level);this.setCustomName(Component.literal("Mafya"));this.setCustomNameVisible(true);this.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));}public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.26D).add(Attributes.ATTACK_DAMAGE, 10.0D);}@Overrideprotected void registerGoals() {this.goalSelector.addGoal(1, new FloatGoal(this));this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));this.targetSelector.addGoal(1, new HurtByTargetGoal(this));this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));}}// =========================================================================// 7. ALİ KEMAL, ŞANZİMENT & GÜLİBİK// =========================================================================public static class AliKemalEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/ali_kemal.png");protected AliKemalEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.28D);}@Overridepublic boolean hurt(DamageSource source, float amount) {boolean flag = super.hurt(source, amount);if (flag && !this.level().isClientSide()) {this.level().setBlockAndUpdate(this.blockPosition().above(4), Blocks.DAMAGED_ANVIL.defaultBlockState());this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, Level.ExplosionInteraction.TNT);}return flag;}}public static class SanzimentEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/sanziment.png");protected SanzimentEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, 0.28D).add(Attributes.ATTACK_DAMAGE, 20.0D);}@Overrideprotected void registerGoals() {this.goalSelector.addGoal(1, new FloatGoal(this));this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, SinanEntity.class, true));}}public static class GulibikEntity extends Monster {public static final ResourceLocation OFFLINE_TEXTURE = new ResourceLocation(AkasyaDuragiMod.MODID, "textures/entity/gulibik.png");protected GulibikEntity(EntityType<? extends Monster> type, Level level) { super(type, level); }public static AttributeSupplier.Builder createAttributes() {return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);}}// =========================================================================// MOD EVENTS: 200 Can Sistemi HUD// =========================================================================public static class ModEvents {@SubscribeEventpublic void onPlayerSpawn(EntityEvent.EnteringSection event) {if (event.getEntity() instanceof Player player) {var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);if (maxHealth != null && maxHealth.getBaseValue() < 200.0D) {maxHealth.setBaseValue(200.0D); player.setHealth(200.0F);}}}}}
